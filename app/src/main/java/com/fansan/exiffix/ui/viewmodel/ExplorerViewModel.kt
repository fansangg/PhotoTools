package com.fansan.exiffix.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/20
 */

class ExplorerViewModel : ViewModel() {

	var currentSelectedPath = PathUtils.getExternalStoragePath()
	var parentPath:String = ""
	var confirmPath = MutableStateFlow("")
	val fileCacheMap = mutableMapOf<String,List<File>>()

	fun isRoot(path:String):Boolean = path == PathUtils.getExternalStoragePath()

	fun getFiles(dir:String):List<File>{
		currentSelectedPath = dir
		return if (fileCacheMap[currentSelectedPath].isNullOrEmpty()) {
			val list = FileUtils.listFilesInDir(currentSelectedPath, false)
			fileCacheMap[currentSelectedPath] = list
			list
		}else fileCacheMap[currentSelectedPath]!!
	}
}