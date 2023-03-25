package com.fansan.picdatemodify.ui.viewmodel

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fansan.picdatemodify.common.logd
import com.fansan.picdatemodify.entity.ImageInfoEntity
import kotlin.math.abs

/**
 *@author  fansan
 *@version 2023/3/25
 */

class ChooseRenameViewModel:ViewModel() {

	val photos = mutableStateListOf<ImageInfoEntity>()
	var queryDone by mutableStateOf(false)

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
}