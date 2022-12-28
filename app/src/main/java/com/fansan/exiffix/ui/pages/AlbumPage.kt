package com.fansan.exiffix.ui.pages

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore.*
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.loader.content.CursorLoader
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.AlbumEntity
import com.fansan.exiffix.ui.entity.ImageInfoEntity
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 *@author  fansan
 *@version 2022/12/26
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlbumPage(navHostController: NavHostController) {
	val context = LocalContext.current
	val readPermissionState = rememberMultiplePermissionsState(permissions = if (Build.VERSION.SDK_INT >= 33) listOf(
		Manifest.permission.READ_MEDIA_IMAGES,
		Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.ACCESS_MEDIA_LOCATION) else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
	)
	TitleColumn(title = "相册", backClick = { navHostController.popBackStack() }) {
		if (readPermissionState.allPermissionsGranted){
			val albumEntityMap = remember {
				getAlbums(context)
			}
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(vertical = 12.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				items(albumEntityMap.keys.toList()) {
					AlbumCard(albumEntity = albumEntityMap.getValue(it)) {

					}
				}
			}
		}else{
			val textToShow = if (readPermissionState.shouldShowRationale) {
				"读取媒体文件需要存储权限,允许该权限才能正常工作"
			} else {
				"读取媒体文件需要存储权限\n请允许此权限"
			}
			Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { 	Text(textToShow)
				SpacerH(height = 20.dp)
				ElevatedButton(onClick = { readPermissionState.launchMultiplePermissionRequest()}) {
					Text("申请权限")
				}
			}
		}
	}
	/*val launcher =
		rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
		                                  onResult = {

		                                  })*/

	/*DisposableEffect(key1 = Unit, effect = {
		val list = getAllUris(context)
		val editPendingIntent = createWriteRequest(context.contentResolver, list)
		launcher.launch(IntentSenderRequest.Builder(editPendingIntent).build())
		onDispose { }
	})*/
}


private fun getAlbums(context: Context): Map<String, AlbumEntity> {
	var cursor: Cursor? = null
	val albumMap = hashMapOf<String, AlbumEntity>()
	val uri = Images.Media.EXTERNAL_CONTENT_URI
	val projection = arrayOf(
		Images.Media.BUCKET_DISPLAY_NAME,
		Images.Media.DATA,
		Images.Media.DATE_MODIFIED,
		Images.Media.DATE_TAKEN,
		Images.Media._ID,
		Images.Media.WIDTH,
		Images.Media.HEIGHT,
		Images.Media.SIZE,
		Images.Media.DISPLAY_NAME
	)
	try {
		cursor = CursorLoader(context, uri, projection, null, null, null).loadInBackground()
		if (cursor != null && cursor.moveToFirst()) {
			do {
				val bucketDisplayNameIndex =
					cursor.getColumnIndexOrThrow(Images.Media.BUCKET_DISPLAY_NAME)
				val dataIndex = cursor.getColumnIndexOrThrow(Images.Media.DATA)
				val modifiedIndex = cursor.getColumnIndexOrThrow(Images.Media.DATE_MODIFIED)
				val dateTakenIndex = cursor.getColumnIndexOrThrow(Images.Media.DATE_TAKEN)
				val idIndex = cursor.getColumnIndexOrThrow(Images.Media._ID)
				val titleIndex = cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME)
				val widthIndex = cursor.getColumnIndexOrThrow(Images.Media.WIDTH)
				val heightIndex = cursor.getColumnIndexOrThrow(Images.Media.HEIGHT)
				val sizeIndex = cursor.getColumnIndexOrThrow(Images.Media.SIZE)
				val bucketDisplayName = cursor.getString(bucketDisplayNameIndex)
				val imgData = cursor.getString(dataIndex)
				val lastModified = cursor.getLong(modifiedIndex)
				val dateTaken = cursor.getLong(dateTakenIndex)
				val title = cursor.getString(titleIndex)
				val width = cursor.getInt(widthIndex)
				val height = cursor.getInt(heightIndex)
				val size = cursor.getLong(sizeIndex)
				val id = cursor.getLong(idIndex)
				val imgUri = ContentUris.withAppendedId(Images.Media.EXTERNAL_CONTENT_URI, id)                /*if (title == "6666.jpg"){
					val test = File(imgData)
					val contentValues = ContentValues()
					val pendingContentValues = ContentValues()
					pendingContentValues.put(MediaColumns.IS_PENDING,1)
					context.contentResolver.update(imgUri,pendingContentValues,null,null)
					contentValues.put(Images.Media.DATE_MODIFIED,System.currentTimeMillis() / 1000)
					contentValues.put(MediaColumns.IS_PENDING,0)
					context.contentResolver.update(imgUri,contentValues,null,null)
				}*/

				val imageInfoEntity = ImageInfoEntity(
					displayName = title,
					width = width,
					height = height,
					taken = dateTaken,
					lastModified = lastModified,
					path = imgData,
					uri = imgUri,
					size = size
				)
				if (!albumMap.containsKey(bucketDisplayName)) {
					albumMap[bucketDisplayName] = AlbumEntity(
						bucketDisplayName, arrayListOf(imageInfoEntity)
					)
				} else {
					albumMap[bucketDisplayName]?.imgList?.add(imageInfoEntity)
				}
			} while (cursor.moveToNext())
		}
	} catch (e: Exception) {
		e.message?.logd()
	} finally {
		cursor?.close()
	}
	return albumMap
}


private fun getCount(context: Context, name: String): Int {
	val uri = Images.Media.EXTERNAL_CONTENT_URI
	val cursor = CursorLoader(
		context, uri, null, "${MediaColumns.BUCKET_DISPLAY_NAME}=?", arrayOf(name), null
	).loadInBackground()
	return if (cursor == null || !cursor.moveToFirst()) 0 else cursor.count
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumCard(albumEntity: AlbumEntity, click: () -> Unit) {
	ElevatedCard(
		onClick = { click.invoke() }, modifier = Modifier
			.padding(horizontal = 12.dp)
			.fillMaxWidth()
	) {
		/*model = LocalContext.current.contentResolver.loadThumbnail(
			albumEntity.imgList[0].uri, Size(300, 300), CancellationSignal()
		),*/
		Row {
			AsyncImage(
				model = albumEntity.imgList[0].uri,
				contentDescription = "Thumbial",
				modifier = Modifier.size(80.dp),
				contentScale = ContentScale.Crop
			)

			SpacerW(width = 12.dp)

			Text(
				text = "${albumEntity.albumName}(${albumEntity.imgList.size})",
				modifier = Modifier.align(alignment = Alignment.CenterVertically),
				fontSize = 16.sp,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}