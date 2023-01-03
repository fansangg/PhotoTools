package com.fansan.exiffix.ui.viewmodel

import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.loader.content.CursorLoader
import com.fansan.exiffix.common.logd
import com.fansan.exiffix.entity.NewAlbumEntity

class AlbumViewModel:ViewModel() {

	val newAlbumMap = mutableMapOf<String, NewAlbumEntity>()
	var firstImg = ""
	var allImageCount = 0
	val allDone = mutableStateOf(false)

	fun getAlbums(context: Context) {
		if (newAlbumMap.isNotEmpty()) return
		val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		val projection = arrayOf(
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
			MediaStore.Images.Media.DATA,
		)
		try {
			context.contentResolver.query(
				uri,
				projection,
				null,
				null,
				"${MediaStore.Images.Media.DATE_MODIFIED} DESC"
			)?.use {
				if (it.moveToFirst()) {
					do {
						val bucketDisplayNameIndex =
							it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
						val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
						val bucketDisplayName = it.getString(bucketDisplayNameIndex)
						val imgData = it.getString(dataIndex)

						allImageCount++
						if (it.isFirst) {
							firstImg = imgData
						}
						if (newAlbumMap.containsKey(bucketDisplayName)) continue
						val imgCount = getCount(context, bucketDisplayName)
						val newAlbumEntity = NewAlbumEntity(bucketDisplayName, imgData, imgCount)
						newAlbumMap[bucketDisplayName] = newAlbumEntity
					} while (it.moveToNext())
				}
			}
		} catch (e: Exception) {
			e.message?.logd()
		} finally {
			allDone.value = true
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