package com.fansan.exiffix.ui.pages

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
import com.fansan.exiffix.ui.viewmodel.ExplorerViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 *@author  fansan
 *@version 2022/12/20
 */


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExplorerPage(navHostController: NavHostController) {
	val readPermissionState =
		rememberPermissionState(permission = if (Build.VERSION.SDK_INT >= 33 ) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE)
	val childNavHostController = rememberNavController()
	val viewModel = viewModel<ExplorerViewModel>(LocalContext.current as ComponentActivity)
	TitleColumn(title = "File Explorer", backClick = { navHostController.popBackStack() }) {
		DisposableEffect(key1 = Unit, effect = {
			onDispose {
				viewModel.fileCacheMap.clear()
			}
		})
		if (readPermissionState.status.isGranted) {
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
			val textToShow = if (readPermissionState.status.shouldShowRationale) {
				"需要存储权限,请允许"
			} else {
				"获取文件夹信息需要存储权限" + "请允许此权限"
			}
			Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { 	Text(textToShow)
				SpacerH(height = 20.dp)
				ElevatedButton(onClick = { readPermissionState.launchPermissionRequest() }) {
					Text("Request Permission")
				}
			}
		}
	}
}