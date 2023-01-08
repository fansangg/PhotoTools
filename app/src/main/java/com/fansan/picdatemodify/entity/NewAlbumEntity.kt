package com.fansan.picdatemodify.entity

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

/**
 *@author  fansan
 *@version 2023/1/3
 */

@Stable
@Parcelize
data class NewAlbumEntity(val albumName:String,val firstImg:String,val count:Int):Parcelable