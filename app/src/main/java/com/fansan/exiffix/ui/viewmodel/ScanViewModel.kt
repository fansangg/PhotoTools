package com.fansan.exiffix.ui.viewmodel

import android.media.ExifInterface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.entity.ErrorType
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.coroutines.coroutineContext
import kotlin.math.ceil
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 *@author  fansan
 *@version 2022/12/21
 */

class ScanViewModel:ViewModel() {

	var scanProgress by mutableStateOf(0f)
	var currentIndex = 0
	val matchFileList = mutableStateListOf<ErrorFile>()
	var currentExecFileName = mutableStateOf("")
	var totalFileSize = 0
	private val mutex = Mutex()

	suspend fun scanFiles(path:String){
		val time = measureTimeMillis {
			val files = FileUtils.listFilesInDirWithFilter(path) {
				it.isFile && (it.name.endsWith(".png") || it.name.endsWith(".jpg") || it.name.endsWith(
					".jepg"
				) || it.name.endsWith(
					".HEIC"
				) || it.name.endsWith(".JPG") || it.name.endsWith(".PNG") || it.name.endsWith(".JEPG"))
			}
			totalFileSize = files.size
			slipList(files)
		}

		"耗时： == $time".logd()

	}

	private suspend fun slipList(list: List<File>) {
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

	private suspend fun analysisFiles(list: List<File>) {
		"analysisFiles".logd()
		list.forEach { file ->
			currentIndex++
			currentExecFileName.value = file.name
			scanProgress = currentIndex / totalFileSize.toFloat()
			"currentIndex == $currentIndex".logd()
			try {
				val exifInterface = ExifInterface(file)
				val dataTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
				if (dataTime != null) {
					val millis = TimeUtils.string2Millis(dataTime, "yyyy:MM:dd HH:mm:ss")
					if (file.lastModified() != millis) {
						matchFileList.add(ErrorFile(ErrorType.DATENOMATCH, file.absolutePath))
					}
				} else {
					matchFileList.add(ErrorFile(ErrorType.NOEXIF, file.absolutePath))
				}
			} catch (e: Exception) {
				matchFileList.add(ErrorFile(ErrorType.OTHERERROR, file.absolutePath))
			}
			delay(10)
		}
	}
}