package com.fansan.exiffix.ui.viewmodel

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.loader.content.CursorLoader
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.common.logd
import com.fansan.exiffix.entity.ModifiedNameEntity
import com.fansan.exiffix.entity.NewAlbumEntity
import com.fansan.exiffix.ui.state.ModifyFileNameState

class AlbumViewModel : ViewModel() {

	val newAlbumMap = mutableMapOf<String, NewAlbumEntity>()
	var firstImg = ""
	var allImageCount = 0
	val allDone = mutableStateOf(false)

	val modifyFileNameState = ModifyFileNameState()

	fun getAlbums(context: Context) {
		if (newAlbumMap.isNotEmpty()) return
		val uri = Media.EXTERNAL_CONTENT_URI
		val projection = arrayOf(
			Media.BUCKET_DISPLAY_NAME,
			Media.DATA,
		)
		try {
			context.contentResolver.query(
				uri, projection, null, null, "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
			)?.use {
				if (it.moveToFirst()) {
					do {
						val bucketDisplayNameIndex =
							it.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME)
						val dataIndex = it.getColumnIndexOrThrow(Media.DATA)
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
		val uri = Media.EXTERNAL_CONTENT_URI
		val cursor = CursorLoader(
			context,
			uri,
			null,
			"${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME}=?",
			arrayOf(name),
			null
		).loadInBackground()
		return if (cursor == null || !cursor.moveToFirst()) 0 else cursor.count
	}

	fun modifiedFileNames(context: Context) {
		val list = getPhotoByAlbumName(context, modifyFileNameState.selectedAlbumName)
		modifyFileNameState.setListCount(list.size)
		list.forEachIndexed { _, modifiedNameEntity ->
			if (modifiedNameEntity.takenTime <= 0){
				if (modifyFileNameState.useTaken && modifyFileNameState.skipNoTaken){
					modifyFileNameState.increaseCurrentIndex()
					modifyFileNameState.increaseSkipCount()
					return@forEachIndexed
				}
			}
			val formatTime = if (modifyFileNameState.useTaken && modifiedNameEntity.takenTime > 0)
				modifiedNameEntity.takenTime else modifiedNameEntity.modifiedTime
			val contentValues = ContentValues()
			val paddingValue = ContentValues()
			val newDisplayName =  java.lang.StringBuilder()
			if (modifyFileNameState.prefix.isNotEmpty()) {
				newDisplayName.append(modifyFileNameState.prefix)
				newDisplayName.append(modifyFileNameState.symbol)
			}
			val timeString = TimeUtils.millis2String(formatTime,modifyFileNameState.format)
			newDisplayName.append(timeString)
			if (modifiedNameEntity.displayName == newDisplayName.toString()){
				modifyFileNameState.increaseCurrentIndex()
				return@forEachIndexed
			}
			//paddingValue.put(Media.IS_PENDING,1)
			contentValues.put(Media.DISPLAY_NAME,newDisplayName.toString())
			//contentValues.put(Media.IS_PENDING,0)
			//context.contentResolver.update(Uri.parse(modifiedNameEntity.uri),paddingValue,null,null)
			val result = context.contentResolver.update(Uri.parse(modifiedNameEntity.uri),contentValues,null,null)
			if (result > 0){
				modifyFileNameState.increaseSuccess()
			}else modifyFileNameState.increaseFailed()
			modifyFileNameState.increaseCurrentIndex()
		}
		modifyFileNameState.taskDone()
	}

	fun getPhotoByAlbumName(context: Context, albumName: String):List<ModifiedNameEntity>{
		val list = mutableListOf<ModifiedNameEntity>()
		val projection = arrayOf(
			Media._ID,
			Media.DISPLAY_NAME,
			Media.DATE_MODIFIED,
			Media.DATE_TAKEN
		)
		context.contentResolver.query(
			Media.EXTERNAL_CONTENT_URI,
			projection,
			"${Media.BUCKET_DISPLAY_NAME}=?",
			arrayOf(albumName),
			null
		)?.use {
			if (it.moveToFirst()) {
				do {
					val idIndex = it.getColumnIndexOrThrow(Media._ID)
					val nameIndex = it.getColumnIndexOrThrow(Media.DISPLAY_NAME)
					val modifiedIndex = it.getColumnIndexOrThrow(Media.DATE_MODIFIED)
					val takenIndex = it.getColumnIndexOrThrow(Media.DATE_TAKEN)
					val mediaId = it.getLong(idIndex)
					val name = it.getString(nameIndex)
					val modified = it.getLong(modifiedIndex)
					val taken = it.getLong(takenIndex)
					val uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, mediaId)
					val entity = ModifiedNameEntity(uri = uri.toString(), modifiedTime = modified, displayName = name, takenTime = taken)
					list.add(entity)
				} while (it.moveToNext())
			}
		}

		return list
	}

}