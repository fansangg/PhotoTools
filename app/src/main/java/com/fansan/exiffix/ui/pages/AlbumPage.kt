package com.fansan.exiffix.ui.pages

import android.Manifest
import android.os.Build
import android.provider.MediaStore.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.fansan.exiffix.common.LoadingStyle2
import com.fansan.exiffix.entity.NewAlbumEntity
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
	val readPermissionState = rememberMultiplePermissionsState(
		permissions = if (Build.VERSION.SDK_INT >= 33) listOf(
			Manifest.permission.READ_MEDIA_IMAGES,
			Manifest.permission.READ_MEDIA_VIDEO,
			Manifest.permission.ACCESS_MEDIA_LOCATION
		) else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
	)
	val viewModel = viewModel<AlbumViewModel>()

	if (readPermissionState.allPermissionsGranted) {
		viewModel.getAlbums(context)
	}

	TitleColumn(title = "相册", backClick = { navHostController.popBackStack() }) {
		if (readPermissionState.allPermissionsGranted) {
			if (viewModel.allDone.value) {
				if (viewModel.newAlbumMap.isEmpty()) {
					EmptyDir(modifier = Modifier.fillMaxSize(), tips = "没有找到文件")
				} else {
					LazyColumn(
						modifier = Modifier.fillMaxSize(),
						contentPadding = PaddingValues(vertical = 12.dp),
						verticalArrangement = Arrangement.spacedBy(12.dp)
					) {
						item {
							val allPhotoEntity =
								NewAlbumEntity("所有照片", viewModel.firstImg, viewModel.allImageCount)
							AlbumCard(albumEntity = allPhotoEntity) {
								navHostController.navigate("PhotoPage/_allImgs")
							}
						}
						items(viewModel.newAlbumMap.keys.toList()) {
							val entity = viewModel.newAlbumMap.getValue(it)
							AlbumCard(albumEntity = entity) {
								navHostController.navigate("PhotoPage/${entity.albumName}")
							}
						}
					}
				}
			} else {
				LoadingStyle2()
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumCard(albumEntity: NewAlbumEntity, click: () -> Unit) {
	ElevatedCard(
		onClick = { click.invoke() }, modifier = Modifier
			.padding(horizontal = 12.dp)
			.fillMaxWidth()
	) {
		Row {
			AsyncImage(
				model = albumEntity.firstImg,
				contentDescription = "Thumbial",
				modifier = Modifier.size(80.dp),
				contentScale = ContentScale.Crop
			)

			SpacerW(width = 12.dp)

			Text(
				text = "${albumEntity.albumName}(${albumEntity.count})",
				modifier = Modifier.align(alignment = Alignment.CenterVertically),
				fontSize = 16.sp,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}