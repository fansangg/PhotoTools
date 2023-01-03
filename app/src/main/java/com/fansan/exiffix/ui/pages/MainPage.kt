package com.fansan.exiffix.ui.pages

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blankj.utilcode.util.ClickUtils
import com.fansan.exiffix.R
import com.fansan.exiffix.ui.viewmodel.ExplorerViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn
import kotlinx.coroutines.launch

/**
 *@author  fansan
 *@version 2022/12/20
 */

@Composable
fun MainPage(navHostController: NavHostController) {
	BackHandler()
	TitleColumn(title = "Home", withBackIcon = false, backClick = { }) {
		
		SpacerH(height = 40.dp)
		ElevatedCard(modifier = Modifier
			.align(alignment = Alignment.CenterHorizontally)
			.padding(horizontal = 12.dp, vertical = 8.dp)
			.clickable {
				navHostController.navigate("ALBUM")
			}) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				Image(painter = painterResource(id = R.mipmap.gallery), contentDescription = "icon")
				Text(text = "选择相册", modifier = Modifier.padding(vertical = 12.dp))
			}
		}
	}

}

@Composable
fun BackHandler() {
	BackHandler {
		ClickUtils.back2HomeFriendly("再按一次退出")
	}
}