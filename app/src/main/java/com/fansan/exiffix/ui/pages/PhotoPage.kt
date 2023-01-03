package com.fansan.exiffix.ui.pages

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blankj.utilcode.util.GsonUtils
import com.fansan.exiffix.common.LoadingStyle2
import com.fansan.exiffix.common.TipDialog
import com.fansan.exiffix.entity.ImageInfoEntity
import com.fansan.exiffix.ui.viewmodel.PhotoPageViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn

/**
 *@author  fansan
 *@version 2022/12/30
 */

@Composable
fun PhotoPage(navHostController: NavHostController, albumName: String) {
	val viewModel = viewModel<PhotoPageViewModel>()

	val context = LocalContext.current
	LaunchedEffect(key1 = Unit, block = {
		if (viewModel.allDone.value.not()) {
			viewModel.getPhotos(context, albumName)
		}
	})

	val tipDialogShow = rememberSaveable {
		mutableStateOf(true)
	}

	TitleColumn(title = if (albumName == "_allImgs") "所有照片" else albumName,
	            backClick = { navHostController.popBackStack() }) {
		if (viewModel.allDone.value) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				LazyVerticalGrid(
					columns = GridCells.Fixed(4),
					modifier = Modifier.fillMaxSize(),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
					contentPadding = PaddingValues(12.dp)
				) {
					items(viewModel.errorPhotoList, key = { it.path }) {
						ImageItem(info = it) {
							navHostController.navigate("DETAILSPAGE/${Uri.encode(GsonUtils.toJson(it))}")
						}
					}
				}

				if (tipDialogShow.value) {
					val tips =
						if (viewModel.errorPhotoList.isNotEmpty()) "共找到${viewModel.errorPhotoList.size}张异常照片" else "真棒，所有照片均无异常"
					val icon =
						if (viewModel.errorPhotoList.isNotEmpty()) Icons.Default.DoneAll else Icons.Default.ThumbUp
					TipDialog(
						tips = tips, icons = icon
					) {
						if (viewModel.errorPhotoList.isNotEmpty()) tipDialogShow.value = false
						else navHostController.popBackStack()
					}
				}
			}
		} else {
			LoadingStyle2()
		}
	}

}


@Composable
fun AnalysisDialog(progress:Float, currentIndex: Int, total: Int, currentFileName: String,findSize:Int,confrimClick:() -> Unit) {
	var doneFlag by rememberSaveable {
		mutableStateOf(false)
	}
	if (progress >=1){
		doneFlag = true
	}
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = Color(0x33000000)),
		contentAlignment = Alignment.Center
	) {

		ElevatedCard(
			modifier = Modifier
				.fillMaxWidth(.7f)
				.aspectRatio(3 / 4f)
		) {

			Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
				SpacerH(height = 12.dp)
				Text(text = if (progress >= 1) "完成" else "正在分析中...", fontSize = 22.sp)
				SpacerH(height = 12.dp)
				Box(modifier = Modifier.fillMaxSize(.75f), contentAlignment = Alignment.Center) {
					if (doneFlag){
						Text(text = "共发现${findSize}张缺少日期或者不匹配的照片", textAlign = TextAlign.Center)
					}else{
						Box(contentAlignment = Alignment.Center) {
							CircularProgressIndicator(
								progress = progress,
								modifier = Modifier
									.fillMaxWidth(.8f)
									.aspectRatio(1f, true),
								strokeWidth = 8.dp
							)

							Text(text = "$currentIndex/$total", fontSize = 18.sp)
						}
					}
				}
				SpacerH(height = 12.dp)
				if (progress >= 1) {
					ElevatedButton(onClick = { confrimClick.invoke() }) {
						Text(text = "查看")
					}
				}else {
					Text(
						text = currentFileName,
						fontSize = 16.sp,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1,
						modifier = Modifier.padding(horizontal = 12.dp)
					)
				}
			}
		}
	}
}

@Composable
fun ImageItem(info: ImageInfoEntity, click: () -> Unit) {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(1f)
			.clickable(onClick = click)
	) {
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current).data(info.path).crossfade(true)
				.build(),
			contentDescription = "img",
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop,
			filterQuality = FilterQuality.None
		)
	}
}

@Composable
@Preview
fun PreviewDialog() {
	AnalysisDialog(
		progress = 1f , currentIndex = 1, total = 100, findSize = 10, currentFileName = "hahahhahahahsdhahda.jpg"
	){

	}
}