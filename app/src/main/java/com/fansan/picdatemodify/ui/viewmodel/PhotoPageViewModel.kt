package com.fansan.picdatemodify.ui.viewmodel

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fansan.picdatemodify.common.logd
import com.fansan.picdatemodify.entity.ImageInfoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlin.math.ceil

/**
 *@author  fansan
 *@version 2022/12/30
 */

class PhotoPageViewModel:ViewModel() {

	var scanProgress by mutableStateOf(0f)
	var currentIndex by mutableStateOf(1)
	var currentExecFileName = ""
	var successFileList = mutableListOf<String>()
	var failedCount = 0
	val errorPhotoList = mutableStateListOf<ImageInfoEntity>()
	val allDone = mutableStateOf(false)
	val allFixDone = mutableStateOf(false)

	fun getPhotos(context: Context, name: String) {
		errorPhotoList.clear()
		val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		val projection = arrayOf(
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media.DATE_MODIFIED,
			MediaStore.Images.Media.DATE_ADDED,
			MediaStore.Images.Media.WIDTH,
			MediaStore.Images.Media.HEIGHT,
			MediaStore.Images.Media.SIZE,
			MediaStore.Images.Media.DISPLAY_NAME,
			MediaStore.Images.Media._ID
		)

		context.contentResolver.query(
			uri,
			projection,
			if (name != "_allImgs") "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME}=?" else null,
			if (name != "_allImgs") arrayOf(name) else null,
			"${MediaStore.Images.Media.DATE_MODIFIED} DESC"
		)?.use {
			if (it.moveToFirst()){
				try {
					do {
						val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
						val takenIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
						val modifiedIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
						val addedIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
						val widthIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
						val heightIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
						val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
						val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
						val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

						val taken = it.getLong(takenIndex)
						val modified = it.getLong(modifiedIndex)
						if (taken <= 0L || abs(modified * 1000 - taken) < 1000 * 60 )
							continue
						val data = it.getString(dataIndex)
						val added = it.getLong(addedIndex)
						val width = it.getInt(widthIndex)
						val height = it.getInt(heightIndex)
						val size = it.getLong(sizeIndex)
						val fileName = it.getString(nameIndex)
						val id = it.getLong(idIndex)
						val fileUri = ContentUris.withAppendedId(uri,id)
						val imageInfoEntity = ImageInfoEntity(displayName = fileName,width,height,taken,modified,data,size,fileUri.toString(),added)
						errorPhotoList.add(imageInfoEntity)

					}while (it.moveToNext())
				} catch (e: Exception) {
					e.message?.logd()
				} finally {
					allDone.value = true
				}
			}
		}
		//slipList()
	}

	private suspend fun slipList() {
		if (errorPhotoList.size > 300) {
			val result = ceil(errorPhotoList.size / 5.0).toInt()
			val newList = errorPhotoList.chunked(result)
			newList.forEach {
				with(CoroutineScope(coroutineContext)) {
					launch {
						fixLastModified(it)
					}
				}
			}
		} else {
			fixLastModified(errorPhotoList)
		}
	}

	private suspend fun fixLastModified(list: List<ImageInfoEntity>) {
		list.forEach { entity ->
			val file = File(entity.path)
			val result = file.setLastModified(entity.taken)
			synchronized(this) {
				currentIndex++
				currentExecFileName = entity.displayName
				scanProgress = currentIndex / errorPhotoList.size.toFloat()
				if (result)
					successFileList.add(entity.path)
				else failedCount++
				if (currentIndex >= errorPhotoList.size){
					allFixDone.value = true
				}
			}
			//delay(3000)
			//yield()
		}
	}

	fun scanFile(context: Context){
		MediaScannerConnection.scanFile(context,successFileList.toTypedArray(),null){
			path,_ ->
		}
		errorPhotoList.removeAll(errorPhotoList.filter { successFileList.contains(it.path) })
	}

	fun fixAll(){
		viewModelScope.launch(Dispatchers.IO){
			slipList()
		}
	}
}