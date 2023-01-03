package com.fansan.exiffix.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *@author  fansan
 *@version 2022/12/27
 */

@Parcelize
data class ImageInfoEntity(val displayName:String,val width:Int,val height:Int,val taken:Long,val lastModified:Long,val path:String,val size:Long,val uri:String,val added:Long):Parcelable