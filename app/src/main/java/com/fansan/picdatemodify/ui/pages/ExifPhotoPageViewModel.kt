package com.fansan.picdatemodify.ui.pages

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.LogUtils
import com.fansan.picdatemodify.entity.ImageInfoEntity
import com.fansan.picdatemodify.entity.PhotoListType

/**
 *@author  fansan
 *@version 2023/4/3
 */

class ExifPhotoPageViewModel:ViewModel() {

	val photoList = mutableStateListOf<ImageInfoEntity>()
	var allDone by mutableStateOf(false)
	var currentType by mutableStateOf(PhotoListType.Single)

	fun getPhotoByAlbumName(c:Context,albumName:String){

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
			MediaStore.Images.Media._ID,
		)

		c.contentResolver.query(uri,projection,if (albumName != "_allImgs") "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME}=?" else null,
		                        if (albumName != "_allImgs") arrayOf(albumName) else null,"${MediaStore.Images.Media.DATE_MODIFIED} DESC")
			?.use {
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
							val data = it.getString(dataIndex)
							val added = it.getLong(addedIndex)
							val width = it.getInt(widthIndex)
							val height = it.getInt(heightIndex)
							val size = it.getLong(sizeIndex)
							val fileName = it.getString(nameIndex)
							val id = it.getLong(idIndex)
							val fileUri = ContentUris.withAppendedId(uri, id)
							val imageInfoEntity = ImageInfoEntity(displayName = fileName,width,height,taken,modified,data,size,fileUri.toString(),added)
							photoList.add(imageInfoEntity)
						}while (it.moveToNext())

					} catch (e: Exception) {
						LogUtils.d("e == ${e.message}")
					} finally {
						allDone = true
					}
				}
			}
	}
}