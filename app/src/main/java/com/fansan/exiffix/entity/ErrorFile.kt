package com.fansan.exiffix.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *@author  fansan
 *@version 2022/12/21
 */

@Parcelize
data class ErrorFile(val type: ErrorType, val path:String, val exifDate:String? = null):Parcelable

enum class ErrorType{
	NOEXIF,
	DATENOMATCH,
	OTHERERROR
}