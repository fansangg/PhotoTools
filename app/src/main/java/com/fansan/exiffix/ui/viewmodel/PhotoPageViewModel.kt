package com.fansan.exiffix.ui.viewmodel

import android.media.ExifInterface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.entity.ErrorType
import com.fansan.exiffix.ui.entity.ImageInfoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.coroutines.coroutineContext
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

	suspend fun slipList(list: List<ImageInfoEntity>) {
		if (list.size > 100) {
			val result = ceil(list.size / 5.0).toInt()
			val newList = list.chunked(result)
			newList.forEach {
				with(CoroutineScope(coroutineContext)){
					launch {
						analysisFiles(it)
					}
				}
			}
		} else {
			analysisFiles(list)
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