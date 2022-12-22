package com.fansan.exiffix.ui.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *@author  fansan
 *@version 2022/12/21
 */

@Parcelize
data class ErrorFile(val type:ErrorType,val path:String):Parcelable

enum class ErrorType{
	NOEXIF,
	DATENOMATCH,
	OTHERERROR
}