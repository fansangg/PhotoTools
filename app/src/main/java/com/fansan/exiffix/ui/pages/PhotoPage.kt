package com.fansan.exiffix.ui.pages

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.AlbumEntity
import com.fansan.exiffix.ui.entity.ImageInfoEntity
import com.fansan.exiffix.ui.viewmodel.PhotoPageViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *@author  fansan
 *@version 2022/12/30
 */

@Composable
fun PhotoPage(navHostController: NavHostController, albumEntity: AlbumEntity) {
	val viewModel = viewModel<PhotoPageViewModel>()


	var showDialog by rememberSaveable(navHostController) {
		mutableStateOf(true)
	}

	LaunchedEffect(key1 = Unit, block = {
		if (viewModel.scanProgress == 0f){
			viewModel.totalFileSize = albumEntity.imgList.size
			withContext(Dispatchers.IO) {
				viewModel.slipList(albumEntity.imgList)
			}
		}
	})

	TitleColumn(title = albumEntity.albumName, backClick = { navHostController.popBackStack() }) {

		if (showDialog) {
			AnalysisDialog(
				progress = viewModel.scanProgress,
				currentIndex = viewModel.currentIndex,
				total = viewModel.totalFileSize,
				findSize = viewModel.errorPhotoList.size,
				currentFileName = viewModel.currentExecFileName
			){
				showDialog = false
			}
		}else{
			LazyVerticalGrid(
				columns = GridCells.Fixed(4),
				modifier = Modifier.fillMaxSize(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				contentPadding = PaddingValues(12.dp)
			) {
				items(viewModel.errorPhotoList, key = {it.path}) {
					ImageItem(info = it){
						navHostController.navigate("DETAILSPAGE/${Uri.encode(GsonUtils.toJson(it))}")
					}
				}
			}
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
fun ImageItem(info: ImageInfoEntity,click:() -> Unit) {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(1f)
			.clickable(onClick = click)
	) {
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current).data(info.path).crossfade(true).build(),
			contentDescription = "img",
			modifier = Modifier
				.fillMaxSize(),
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