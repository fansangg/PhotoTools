package com.fansan.exiffix.ui.viewmodel

import android.media.ExifInterface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.entity.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/21
 */

class ScanViewModel:ViewModel() {

	var scanProgress by mutableStateOf(0f)
	val matchFileList = mutableListOf<ErrorFile>()
	var currentExecFileName = mutableStateOf("")
	var totalFileSize = 0

	suspend fun scanFiles(path:String){
		withContext(Dispatchers.IO){
			val files = FileUtils.listFilesInDirWithFilter(path) {
				it.isFile && (it.name.endsWith(".png") || it.name.endsWith(".jpg") || it.name.endsWith(".jepg") || it.name.endsWith(
					".HEIC"
				) || it.name.endsWith(".JPG"))
			}
			totalFileSize = files.size
			files.forEachIndexed { index, file ->
				scanProgress = (index+1) / files.size.toFloat()
				currentExecFileName.value = file.name
				try {
					val exifInterface = ExifInterface(file)
					val dataTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
					if (dataTime!= null){
						val millis =
							TimeUtils.string2Millis(dataTime, "yyyy:MM:dd HH:mm:ss")
						if (file.lastModified() != millis){
							matchFileList.add(ErrorFile(ErrorType.DATENOMATCH,file.absolutePath))
						}
					}else{
						matchFileList.add(ErrorFile(ErrorType.NOEXIF,file.absolutePath))
					}
				} catch (e: Exception) {
					matchFileList.add(ErrorFile(ErrorType.OTHERERROR,file.absolutePath))
				}
			}
		}
	}
}