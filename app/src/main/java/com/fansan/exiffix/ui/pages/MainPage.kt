package com.fansan.exiffix.ui.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blankj.utilcode.util.ClickUtils
import com.fansan.exiffix.R
import com.fansan.exiffix.common.Constants
import com.fansan.exiffix.entity.AlbumType
import com.fansan.exiffix.entity.MainFuncBean
import com.fansan.exiffix.router.Router
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 *@author  fansan
 *@version 2022/12/20
 */

@Composable
fun MainPage(navHostController: NavHostController) {
	val statusBarColor = colorScheme.primary
	val systemUiController = rememberSystemUiController()
	SideEffect {
		systemUiController.setStatusBarColor(statusBarColor)
	}
	BackHandler()
	val itemList = remember {
		listOf(
			MainFuncBean(Constants.MODIFIED_DATE_LABEL, R.mipmap.date),
			MainFuncBean(Constants.EXIF_LABEL, R.mipmap.exif),
			MainFuncBean(Constants.MODIFIED_FILE_NAME_LABEL, R.mipmap.file),
		)
	}
	TitleColumn(title = "欢迎使用", withBackIcon = false, backClick = { }) {
		LazyVerticalGrid(
			columns = GridCells.Fixed(2),
			content = {
				items(itemList){
					FunctionItem(modifier = Modifier
						.fillMaxWidth()
						.aspectRatio(1.2f), bean = it) {
						when(it.name){
							Constants.MODIFIED_DATE_LABEL -> navHostController.navigate("${Router.album}/${AlbumType.DATE}")
							Constants.MODIFIED_FILE_NAME_LABEL -> navHostController.navigate("${Router.album}/${AlbumType.FILENAME}")
						}
					}
				}
			},
			contentPadding = PaddingValues(12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		)
	}

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionItem(modifier: Modifier,bean:MainFuncBean,click: () -> Unit) {
	ElevatedCard(modifier = modifier, onClick = click) {
		Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
			Box(modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.background(color = MaterialTheme.colorScheme.secondary), contentAlignment = Alignment.Center){
				Image(painter = painterResource(id = bean.image), contentDescription = "icon")
			}
			Text(text = bean.name, modifier = Modifier.padding(vertical = 12.dp))
		}
	}
}

@Composable
fun BackHandler() {
	BackHandler {
		ClickUtils.back2HomeFriendly("再按一次退出")
	}
}