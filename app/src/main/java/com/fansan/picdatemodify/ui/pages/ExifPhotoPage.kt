package com.fansan.picdatemodify.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.fansan.picdatemodify.R
import com.fansan.picdatemodify.common.LoadingStyle2
import com.fansan.picdatemodify.common.noRippleClick
import com.fansan.picdatemodify.entity.ImageInfoEntity
import com.fansan.picdatemodify.entity.PhotoListType
import com.fansan.picdatemodify.ui.widgets.SpacerH
import com.fansan.picdatemodify.ui.widgets.SpacerW
import com.fansan.picdatemodify.ui.widgets.TitleColumn

/**
 *@author  fansan
 *@version 2023/4/3
 */

@Composable
fun ExifPhotoPage(navHostController: NavHostController, albumName: String) {

	val context = LocalContext.current
	val vm = viewModel<ExifPhotoPageViewModel>()
	DisposableEffect(key1 = Unit, effect = {
		vm.getPhotoByAlbumName(context, albumName)
		onDispose { }
	})
	TitleColumn(title = if (albumName == "_allImgs") "所有照片" else albumName,
	            backClick = { navHostController.popBackStack() },
	            rightWidget = {
		            Image(
			            painter = painterResource(id = if (vm.currentType == PhotoListType.Single) R.mipmap.grid_icon else R.mipmap.list_icon),
			            contentDescription = "",
			            colorFilter = ColorFilter.tint(Color.White),
			            modifier = Modifier.size(16.dp)
		            )
	            },
	            rightClick = {
		            if (vm.currentType != PhotoListType.Single) vm.currentType =
			            PhotoListType.Single
		            else vm.currentType = PhotoListType.Grid
	            }) {

		if (vm.allDone) {

			when (vm.currentType) {

				PhotoListType.Single -> {
					LazyColumn(
						modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
							top = 12.dp, start = 12.dp, end = 12.dp
						), verticalArrangement = Arrangement.spacedBy(12.dp)
					) {
						items(vm.photoList) {
							SingleImgItem(entity = it) {

							}
						}
					}
				}

				else -> {
					LazyVerticalGrid(
						columns = GridCells.Fixed(3),
						content = {
							items(vm.photoList) {
								AsyncImage(
									model = it.path,
									contentDescription = "",
									modifier = Modifier
										.fillMaxWidth()
										.aspectRatio(1f)
										.clip(
											RoundedCornerShape(9.dp)
										),
									contentScale = ContentScale.Crop
								)
							}
						},
						modifier = Modifier.fillMaxSize(),
						contentPadding = PaddingValues(
							top = 12.dp, start = 12.dp, end = 12.dp
						),
						verticalArrangement = Arrangement.spacedBy(12.dp),
						horizontalArrangement = Arrangement.spacedBy(12.dp)
					)
				}
			}

		} else {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				LoadingStyle2()
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleImgItem(entity: ImageInfoEntity, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(76.dp)
			.background(
				color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(9.dp)
			), verticalAlignment = Alignment.CenterVertically
	) {
		AsyncImage(
			model = entity.path,
			contentDescription = "img",
			modifier = Modifier
				.fillMaxHeight()
				.aspectRatio(1f)
				.clip(
					RoundedCornerShape(topStart = 9.dp, bottomStart = 9.dp)
				),
			contentScale = ContentScale.Crop
		)

		SpacerW(width = 12.dp)

		Text(text = entity.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis)
	}
}