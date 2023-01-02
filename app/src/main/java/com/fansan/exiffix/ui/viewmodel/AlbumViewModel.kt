package com.fansan.exiffix.ui.viewmodel

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.loader.content.CursorLoader
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.AlbumEntity
import com.fansan.exiffix.ui.entity.ImageInfoEntity
import java.util.TreeMap

class AlbumViewModel:ViewModel() {

	val albumMap = mutableMapOf<String, AlbumEntity>()
	val allInfoList = arrayListOf<ImageInfoEntity>()
	fun getAlbums(context: Context) {
		if (albumMap.isNotEmpty()) return
		var cursor: Cursor? = null
		val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		val projection = arrayOf(
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.DATE_MODIFIED,
			MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media._ID,
			MediaStore.Images.Media.WIDTH,
			MediaStore.Images.Media.HEIGHT,
			MediaStore.Images.Media.SIZE,
			MediaStore.Images.Media.DISPLAY_NAME
		)
		try {
			cursor = CursorLoader(context, uri, projection, null, null, "${MediaStore.Images.Media.DATE_MODIFIED} DESC").loadInBackground()
			if (cursor != null && cursor.moveToFirst()) {
				do {
					val bucketDisplayNameIndex =
						cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
					val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
					val modifiedIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
					val dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
					val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
					val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
					val widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
					val heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
					val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
					val bucketDisplayName = cursor.getString(bucketDisplayNameIndex)
					val imgData = cursor.getString(dataIndex)
					val lastModified = cursor.getLong(modifiedIndex)
					val dateTaken = cursor.getLong(dateTakenIndex)
					val title = cursor.getString(titleIndex)
					val width = cursor.getInt(widthIndex)
					val height = cursor.getInt(heightIndex)
					val size = cursor.getLong(sizeIndex)
					val id = cursor.getLong(idIndex)
					val imgUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)                /*if (title == "6666.jpg"){
					val test = File(imgData)
					val contentValues = ContentValues()
					val pendingContentValues = ContentValues()
					pendingContentValues.put(MediaColumns.IS_PENDING,1)
					context.contentResolver.update(imgUri,pendingContentValues,null,null)
					contentValues.put(Images.Media.DATE_MODIFIED,System.currentTimeMillis() / 1000)
					contentValues.put(MediaColumns.IS_PENDING,0)
					context.contentResolver.update(imgUri,contentValues,null,null)
				}*/

					val imageInfoEntity = ImageInfoEntity(
						displayName = title,
						width = width,
						height = height,
						taken = dateTaken,
						lastModified = lastModified,
						path = imgData,
						uri = imgUri.toString(),
						size = size
					)
					if (!albumMap.containsKey(bucketDisplayName)) {
						albumMap[bucketDisplayName] = AlbumEntity(
							bucketDisplayName, arrayListOf(imageInfoEntity)
						)
					} else {
						albumMap[bucketDisplayName]?.imgList?.add(imageInfoEntity)
					}
					allInfoList.add(imageInfoEntity)
				} while (cursor.moveToNext())
			}
		} catch (e: Exception) {
			e.message?.logd()
		} finally {
			cursor?.close()
		}
	}


	private fun getCount(context: Context, name: String): Int {
		val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		val cursor = CursorLoader(
			context, uri, null, "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME}=?", arrayOf(name), null
		).loadInBackground()
		return if (cursor == null || !cursor.moveToFirst()) 0 else cursor.count
	}

}