package com.fansan.picdatemodify.ui.viewmodel

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.TimeUtils
import com.fansan.picdatemodify.common.logd
import com.fansan.picdatemodify.entity.ImageInfoEntity
import com.fansan.picdatemodify.entity.ModifiedNameEntity
import com.fansan.picdatemodify.ui.state.ModifyFileNameState
import kotlin.math.abs

/**
 *@author  fansan
 *@version 2023/3/25
 */

class ChooseRenameViewModel:ViewModel() {

	val photos = mutableStateListOf<ImageInfoEntity>()
	var queryDone by mutableStateOf(false)
	val modifyFileNameState = ModifyFileNameState()

	fun selectedPhotos():List<ModifiedNameEntity>{
		return photos.filter {
			it.selected
		}.map {
			ModifiedNameEntity(uri = it.uri, modifiedTime = it.lastModified, takenTime = it.taken, displayName = it.displayName)
		}
	}

	fun getPhotos(context: Context, name: String) {
		photos.clear()
		val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		val projection = arrayOf(
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media.DATE_MODIFIED,
			MediaStore.Images.Media.DATE_ADDED,
			MediaStore.Images.Media.DISPLAY_NAME,
			MediaStore.Images.Media._ID
		)

		context.contentResolver.query(
			uri,
			projection,
			if (name != "all") "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME}=?" else null,
			if (name != "all") arrayOf(name) else null,
			"${MediaStore.Images.Media.DISPLAY_NAME} DESC"
		)?.use {
			if (it.moveToFirst()){
				try {
					do {
						val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
						val takenIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
						val modifiedIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
						val addedIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
						val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
						val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

						val taken = it.getLong(takenIndex)
						val modified = it.getLong(modifiedIndex)
						val data = it.getString(dataIndex)
						val added = it.getLong(addedIndex)
						val fileName = it.getString(nameIndex)
						val id = it.getLong(idIndex)
						val fileUri = ContentUris.withAppendedId(uri, id)
						val imageInfoEntity = ImageInfoEntity(displayName = fileName,0,0,taken,modified,data,0,fileUri.toString(),added)
						photos.add(imageInfoEntity)
					}while (it.moveToNext())
				} catch (e: Exception) {
					e.message?.logd()
				} finally {
					queryDone = true
				}
			}
		}
		//slipList()
	}

	fun modifiedFileNamesImpl(context: Context) {

		selectedPhotos().forEachIndexed { _, modifiedNameEntity ->
			runCatching {
				if (modifiedNameEntity.takenTime <= 0) {
					if (modifyFileNameState.useTaken && modifyFileNameState.skipNoTaken) {
						synchronized(this@ChooseRenameViewModel){
							modifyFileNameState.increaseCurrentIndex()
							modifyFileNameState.increaseSkipCount()
							return@forEachIndexed
						}
					}
				}
				val formatTime =
					if (modifyFileNameState.useTaken && modifiedNameEntity.takenTime > 0) modifiedNameEntity.takenTime else modifiedNameEntity.modifiedTime * 1000
				val contentValues = ContentValues()
				val paddingValue = ContentValues()
				val newDisplayName = java.lang.StringBuilder()
				if (modifyFileNameState.prefix.isNotEmpty()) {
					newDisplayName.append(modifyFileNameState.prefix)
					newDisplayName.append(modifyFileNameState.symbol)
				}
				val timeString = TimeUtils.millis2String(formatTime, modifyFileNameState.format)
				newDisplayName.append(timeString)
				if (modifiedNameEntity.displayName == newDisplayName.toString()) {
					synchronized(this@ChooseRenameViewModel) {
						modifyFileNameState.increaseSuccess()
						modifyFileNameState.increaseCurrentIndex()
						return@forEachIndexed
					}
				}			//paddingValue.put(Media.IS_PENDING,1)
				contentValues.put(
					MediaStore.Images.Media.DISPLAY_NAME,
					newDisplayName.toString()
				)			//contentValues.put(Media.IS_PENDING,0)
				//context.contentResolver.update(Uri.parse(modifiedNameEntity.uri),paddingValue,null,null)
				val result = context.contentResolver.update(
					Uri.parse(modifiedNameEntity.uri),
					contentValues,
					null,
					null
				)
				synchronized(this@ChooseRenameViewModel) {
					if (result > 0) {
						modifyFileNameState.increaseSuccess()
					} else modifyFileNameState.increaseFailed()
					modifyFileNameState.increaseCurrentIndex()
				}
			}.onFailure {
				synchronized(this@ChooseRenameViewModel) {
					modifyFileNameState.increaseFailed()
					modifyFileNameState.increaseCurrentIndex()
				}
			}
		}

		checkDoen()
	}

	private fun checkDoen(){
		if (modifyFileNameState.modifiedFileNameCurrentIndex.value >= modifyFileNameState.modifiedFileNameListCount.value) {
			modifyFileNameState.taskDone()
		}
	}
}