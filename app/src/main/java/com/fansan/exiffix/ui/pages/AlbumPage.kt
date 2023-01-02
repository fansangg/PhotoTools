package com.fansan.exiffix.ui.pages

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.loader.content.CursorLoader
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.GsonUtils
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.AlbumEntity
import com.fansan.exiffix.ui.entity.ImageInfoEntity
import com.fansan.exiffix.ui.viewmodel.AlbumViewModel
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
	val viewModel = viewModel<AlbumViewModel>()
	viewModel.getAlbums(context)
	TitleColumn(title = "相册", backClick = { navHostController.popBackStack() }) {
		if (readPermissionState.allPermissionsGranted){
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(vertical = 12.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				item {
					val allPhotoEntity = AlbumEntity("所有照片",viewModel.allInfoList)
					AlbumCard(albumEntity = allPhotoEntity) {
						val date = GsonUtils.toJson(allPhotoEntity)
						navHostController.navigate("PhotoPage/${Uri.encode(date)}")
					}
				}
				items(viewModel.albumMap.keys.toList()) {
					AlbumCard(albumEntity = viewModel.albumMap.getValue(it)) {
						val date = GsonUtils.toJson(viewModel.albumMap.getValue(it))
						navHostController.navigate("PhotoPage/${Uri.encode(date)}")
					}
				}
			}
		}else{
			val textToShow = if (readPermissionState.shouldShowRationale) {
				"需要读取照片权限才能正常工作"
			} else {
				"需要读取照片权限才能正常工作\n请允许此权限"
			}
			Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { 	Text(textToShow, textAlign = TextAlign.Center)
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