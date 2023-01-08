package com.fansan.picdatemodify.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *@author  fansan
 *@version 2022/12/26
 */

@Parcelize
data class AlbumEntity(val albumName:String,val imgList:ArrayList<ImageInfoEntity>):Parcelable