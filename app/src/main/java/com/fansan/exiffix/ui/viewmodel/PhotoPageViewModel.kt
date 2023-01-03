package com.fansan.exiffix.ui.viewmodel

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.common.logd
import com.fansan.exiffix.entity.ImageInfoEntity
import kotlinx.coroutines.CoroutineScope
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
	var currentIndex = 0
	var currentExecFileName = ""
	var totalFileSize = 0
	val errorPhotoList = mutableStateListOf<ImageInfoEntity>()
	private val mutex = Mutex()
	val allDone = mutableStateOf(false)

	@SuppressLint("RestrictedApi")
	fun getPhotos(context: Context, name: String) {
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
						if (taken != 0L && modified * 1000 == taken)
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

	suspend fun slipList() {
		if (errorPhotoList.size > 100) {
			val result = ceil(errorPhotoList.size / 5.0).toInt()
			val newList = errorPhotoList.chunked(result)
			newList.forEach {
				with(CoroutineScope(coroutineContext)) {
					launch {
						analysisFiles(it)
					}
				}
			}
		} else {
			analysisFiles(errorPhotoList)
		}
	}

	private suspend fun analysisFiles(list: List<ImageInfoEntity>) {
		list.forEach { entity ->
			val file = File(entity.path)
			mutex.withLock {
				currentIndex++
				currentExecFileName = entity.displayName
				scanProgress = currentIndex / totalFileSize.toFloat()
			}
			try {
				val exifInterface = ExifInterface(file)
				val dataTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
				if (dataTime != null) {
					val millis = TimeUtils.string2Millis(dataTime, "yyyy:MM:dd HH:mm:ss")
					if (file.lastModified() != millis) {
						errorPhotoList.add(entity)
					}
				} else {
					errorPhotoList.add(entity)
				}
			} catch (e: Exception) {
				errorPhotoList.add(entity)
			}
			//yield()
		}
	}
}