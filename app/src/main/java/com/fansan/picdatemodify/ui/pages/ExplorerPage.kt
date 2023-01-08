package com.fansan.picdatemodify.ui.pages

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blankj.utilcode.util.PathUtils
import com.fansan.picdatemodify.ui.viewmodel.ExplorerViewModel
import com.fansan.picdatemodify.ui.widgets.SpacerH
import com.fansan.picdatemodify.ui.widgets.TitleColumn
import com.google.accompanist.permissions.*

/**
 *@author  fansan
 *@version 2022/12/20
 */


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExplorerPage(navHostController: NavHostController) {
	val readPermissionState = rememberMultiplePermissionsState(permissions = if (Build.VERSION.SDK_INT >= 33 ) listOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO) else listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
	val childNavHostController = rememberNavController()
	val viewModel = viewModel<ExplorerViewModel>(LocalContext.current as ComponentActivity)
	TitleColumn(title = "File Explorer", backClick = { navHostController.popBackStack() }) {
		DisposableEffect(key1 = Unit, effect = {
			onDispose {
				viewModel.fileCacheMap.clear()
			}
		})
		if (readPermissionState.allPermissionsGranted) {
			NavHost(
				navController = childNavHostController,
				startDestination = "FILE/{filePath}/{parent}",
				modifier = Modifier.fillMaxSize()
			) {
				composable("FILE/{filePath}/{parent}", arguments = listOf(navArgument("filePath") {
					defaultValue = Uri.encode(PathUtils.getExternalStoragePath())
				}, navArgument("parent") { defaultValue = "" })) {
					ExplorerDesPage(
						navHostController = childNavHostController,
						path = it.arguments?.getString("filePath") ?: "",
						parent = it.arguments?.getString("parent") ?: "",
					){
						navHostController.popBackStack()
					}
				}
			}
		} else {
			val textToShow = if (readPermissionState.shouldShowRationale) {
				"需要存储权限,请允许"
			} else {
				"获取文件夹信息需要存储权限\n请允许此权限"
			}
			Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { 	Text(textToShow)
				SpacerH(height = 20.dp)
				ElevatedButton(onClick = { readPermissionState.launchMultiplePermissionRequest()}) {
					Text("Request Permission")
				}
			}
		}
	}
}