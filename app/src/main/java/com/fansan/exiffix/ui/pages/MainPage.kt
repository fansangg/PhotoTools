package com.fansan.exiffix.ui.pages

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blankj.utilcode.util.ClickUtils
import com.fansan.exiffix.ui.viewmodel.ExplorerViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn
import kotlinx.coroutines.launch

/**
 *@author  fansan
 *@version 2022/12/20
 */

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainPage(navHostController: NavHostController) {
	val viewModel = viewModel<ExplorerViewModel>(LocalContext.current as ComponentActivity)
	val confirmPath = viewModel.confirmPath.collectAsStateWithLifecycle()
	BackHandler()
	TitleColumn(title = "Home", withBackIcon = false, backClick = { }) {
		SpacerH(height = 50.dp)
		Text(
			text = confirmPath.value.ifEmpty { "Select Picture Folder" },
			fontSize = 18.sp,
			fontWeight = FontWeight.SemiBold,
			modifier = Modifier
				.padding(start = 12.dp)
				.fillMaxWidth(),
			textAlign = TextAlign.Center
		)
		SpacerH(height = 40.dp)
		Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

			ElevatedButton(onClick = { navHostController.navigate("EXPLORER") }) {
				Text(text = "Open File Explorer")
			}
		}

		if (confirmPath.value.isNotEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 40.dp),
				contentAlignment = Alignment.Center
			) {
				ElevatedButton(onClick = { navHostController.navigate("SCAN/${Uri.encode(confirmPath.value)}") }) {
					Text(text = "Scan Folder")
				}
			}
		}

	}

}

@Composable
fun BackHandler(){
	BackHandler {
		ClickUtils.back2HomeFriendly("再按一次退出")
	}
}