package com.fansan.exiffix.ui.pages

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.loader.content.CursorLoader
import androidx.navigation.NavHostController
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.AlbumEntity

/**
 *@author  fansan
 *@version 2022/12/26
 */
 
@Composable
fun AlbumPage(navHostController: NavHostController){
	val list = getAlbum(LocalContext.current)
	"list == $list".logd()
}


private fun getAlbum(context:Context):Map<String,AlbumEntity>{
	val albumMap = hashMapOf<String,AlbumEntity>()
	val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
	val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.DATA)
	val cursor = CursorLoader(context,uri,projection,null,null,null)
		.loadInBackground()
	if (cursor != null){
		while (cursor.moveToNext()){
			val bucketDisplayNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
			val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
			val bucketDisplayName = cursor.getString(bucketDisplayNameIndex)
			val imgData = cursor.getString(dataIndex)
			if (!albumMap.containsKey(bucketDisplayName)){
				albumMap[bucketDisplayName] = AlbumEntity(bucketDisplayName, getCount(context,bucketDisplayName),imgData)
			}
		}
	}
	cursor?.close()
	return albumMap
}

private fun getCount(context: Context,name:String):Int{
	val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
	val cursor = CursorLoader(context, uri, null, "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME}=?", arrayOf(name),null)
		.loadInBackground()
	return if (cursor == null || !cursor.moveToFirst()) 0 else cursor.count
}