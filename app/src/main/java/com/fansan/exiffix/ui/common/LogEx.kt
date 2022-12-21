package com.fansan.exiffix.ui.common

import android.annotation.SuppressLint
import com.blankj.utilcode.util.LogUtils
import java.util.*

/**
 *@author  fansan
 *@version 2022/12/21
 */

@SuppressLint("LogNotTimber")
fun String.logd() {
	val methodName = Thread.currentThread().stackTrace[3].methodName
	val  className = Thread.currentThread().stackTrace[3].className
	val fileName = Thread.currentThread().stackTrace[3].fileName
	val number = Thread.currentThread().stackTrace[3].lineNumber
	val infoText = Formatter().format("%s.%s(%s:%d)", className, methodName, fileName, number)
	LogUtils.dTag("fansangg", """$infoText
      |
      |$this
   """.trimMargin())
}