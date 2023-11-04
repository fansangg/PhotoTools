package com.fansan.picdatemodify.ui.viewmodel

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.TimeUtils
import com.fansan.picdatemodify.common.logd
import com.fansan.picdatemodify.entity.ModifiedNameEntity
import com.fansan.picdatemodify.entity.NewAlbumEntity
import com.fansan.picdatemodify.ui.state.ModifyFileNameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil

class AlbumViewModel : ViewModel() {

	val newAlbumList = mutableListOf<NewAlbumEntity>()
	var firstImg = ""
	var allImageCount = 0
	val allDone = mutableStateOf(false)

	val modifyFileNameState = ModifyFileNameState()


	fun getAlbums(context: Context) {
		//if (newAlbumList.isNotEmpty()) return
		newAlbumList.clear()
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
						if (newAlbumList.count { list -> list.albumName == bucketDisplayName } > 0) continue
						val imgCount = getCount(context, bucketDisplayName)
						val newAlbumEntity = NewAlbumEntity(bucketDisplayName, imgData, imgCount)
						newAlbumList.add(newAlbumEntity)
					} while (it.moveToNext())
					newAlbumList.sortByDescending { entity ->
						entity.count
					}
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
		context.contentResolver.query(uri,null,
		                              "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME}=?",
		                              arrayOf(name),
		                              null)
			?.use {
				return it.count
			}
		return 0
	}

	fun modifiedFileNames(context: Context) {
		viewModelScope.launch(Dispatchers.IO) {
			val list = getPhotoByAlbumName(context, modifyFileNameState.selectedAlbumName)
			modifyFileNameState.setListCount(list.size)
			if (list.size > 300) {
				val result = ceil(list.size / 5.0).toInt()
				val newList = list.chunked(result)
				newList.forEach { it ->
					with(CoroutineScope(coroutineContext)) {
						launch {
							modifiedFileNamesImpl(context,it)
						}
					}
				}
			}else{
				modifiedFileNamesImpl(context,list)
			}
		}
	}

	private fun modifiedFileNamesImpl(context: Context, list: List<ModifiedNameEntity>) {

		list.forEachIndexed { _, modifiedNameEntity ->
			runCatching {
				if (modifiedNameEntity.takenTime <= 0) {
					if (modifyFileNameState.useTaken && modifyFileNameState.skipNoTaken) {
						synchronized(this@AlbumViewModel){
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
					synchronized(this@AlbumViewModel) {
						modifyFileNameState.increaseSuccess()
						modifyFileNameState.increaseCurrentIndex()
						return@forEachIndexed
					}
				}			//paddingValue.put(Media.IS_PENDING,1)
				contentValues.put(
					Media.DISPLAY_NAME,
					newDisplayName.toString()
				)			//contentValues.put(Media.IS_PENDING,0)
				//context.contentResolver.update(Uri.parse(modifiedNameEntity.uri),paddingValue,null,null)
				val result = context.contentResolver.update(
					Uri.parse(modifiedNameEntity.uri),
					contentValues,
					null,
					null
				)
				synchronized(this@AlbumViewModel) {
					if (result > 0) {
						modifyFileNameState.increaseSuccess()
					} else modifyFileNameState.increaseFailed()
					modifyFileNameState.increaseCurrentIndex()
				}
			}.onFailure {
				synchronized(this@AlbumViewModel) {
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

	fun getPhotoByAlbumName(context: Context, albumName: String): List<ModifiedNameEntity> {
		val list = mutableListOf<ModifiedNameEntity>()
		val projection = arrayOf(
			Media._ID, Media.DISPLAY_NAME, Media.DATE_MODIFIED, Media.DATE_TAKEN
		)
		context.contentResolver.query(
			Media.EXTERNAL_CONTENT_URI,
			projection,
			if (albumName != "all") "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME}=?" else null,
			if (albumName != "all") arrayOf(albumName) else null,
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
					val entity = ModifiedNameEntity(
						uri = uri.toString(),
						modifiedTime = modified,
						displayName = name,
						takenTime = taken
					)
					list.add(entity)
				} while (it.moveToNext())
			}
		}

		return list
	}

}