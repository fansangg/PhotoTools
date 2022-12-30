package com.fansan.exiffix.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
	LaunchedEffect(key1 = Unit, block = {
		viewModel.totalFileSize = albumEntity.imgList.size
		withContext(Dispatchers.IO) {
			viewModel.slipList(albumEntity.imgList)
		}
	})

	val showDialog by remember {
		derivedStateOf {
			viewModel.scanProgress < 1
		}
	}

	TitleColumn(title = "详情列表", backClick = { navHostController.popBackStack() }) {
		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			modifier = Modifier.fillMaxSize(),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			contentPadding = PaddingValues(12.dp)
		) {
			items(viewModel.errorPhotoList) {
				ImageItem(info = it)
			}
		}
	}

	if (showDialog) {
		AnalysisDialog(
			progress = viewModel.scanProgress,
			currentIndex = viewModel.currentIndex,
			total = viewModel.totalFileSize,
			currentFileName = viewModel.currentExecFileName
		)
	}

}


@Composable
fun AnalysisDialog(progress: Float, currentIndex: Int, total: Int, currentFileName: String) {
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
				Text(text = "正在分析...", fontSize = 22.sp)
				SpacerH(height = 12.dp)
				Box(modifier = Modifier.fillMaxSize(.8f), contentAlignment = Alignment.Center) {
					CircularProgressIndicator(
						progress = progress,
						modifier = Modifier
							.fillMaxWidth(.8f)
							.aspectRatio(1f, true),
						strokeWidth = 8.dp
					)
					Text(text = "$currentIndex/$total", fontSize = 18.sp)
				}
				SpacerH(height = 12.dp)
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

@Composable
fun ImageItem(info: ImageInfoEntity) {
	AsyncImage(
		model = ImageRequest.Builder(LocalContext.current).data(info.path).crossfade(true).build(),
		contentDescription = "img",
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(1f),
		contentScale = ContentScale.Crop,
		filterQuality = FilterQuality.Medium
	)
}

@Composable
@Preview
fun PreviewDialog() {
	AnalysisDialog(
		progress = 0.5f, currentIndex = 1, total = 100, currentFileName = "hahahhahahahsdhahda.jpg"
	)
}