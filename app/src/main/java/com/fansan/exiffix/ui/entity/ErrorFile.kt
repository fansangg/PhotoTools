package com.fansan.exiffix.ui.entity

/**
 *@author  fansan
 *@version 2022/12/21
 */

data class ErrorFile(val type:ErrorType,val path:String)

enum class ErrorType{
	NOEXIF,
	DATENOMATCH,
	OTHERERROR
}