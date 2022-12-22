package com.fansan.exiffix.ui.entity

import android.os.Bundle
import androidx.navigation.NavType
import com.blankj.utilcode.util.GsonUtils

/**
 *@author  fansan
 *@version 2022/12/22
 */

class ErrorFileNavType:NavType<ArrayList<ErrorFile>>(isNullableAllowed = false) {

	override fun get(bundle: Bundle, key: String): ArrayList<ErrorFile>? {
		return bundle.getParcelableArrayList(key)
	}

	override fun parseValue(value: String): ArrayList<ErrorFile> {
		return GsonUtils.fromJson(value, GsonUtils.getListType(ErrorFile::class.java))
	}

	override fun put(bundle: Bundle, key: String, value: ArrayList<ErrorFile>) {
		bundle.putParcelableArrayList(key,value)
	}
}