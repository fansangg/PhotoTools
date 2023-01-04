package com.fansan.exiffix.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blankj.utilcode.util.GsonUtils
import com.fansan.exiffix.common.logd
import com.fansan.exiffix.entity.AlbumEntity
import com.fansan.exiffix.entity.ErrorFile
import com.fansan.exiffix.entity.ErrorFileNavType
import com.fansan.exiffix.entity.ImageInfoEntity
import com.fansan.exiffix.router.Router
import com.fansan.exiffix.ui.pages.*

/**
 *@author  fansan
 *@version 2022/12/20
 */

@Composable
fun ExifFIXNavHost(modifier: Modifier, navController: NavHostController = rememberNavController()) {

	NavHost(modifier = modifier, navController = navController, startDestination = "MAIN") {
		composable("MAIN") {
			MainPage(navHostController = navController)
		}

		composable("EXPLORER") {
			ExplorerPage(navHostController = navController)
		}

		composable("SCAN/{path}", arguments = listOf(navArgument(name = "path") {})) {
			ScanPage(navHostController = navController, it.arguments?.getString("path") ?: "")
		}

		composable(
			"CHECK/{list}", arguments = listOf(navArgument("list") { type = ErrorFileNavType() })
		) {
			val list = it.arguments?.getParcelableArrayList<ErrorFile>("list") ?: arrayListOf()
			CheckListPage(
				navHostController = navController, list = list
			)
		}

		composable("${Router.details}/{data}", arguments = listOf(navArgument("data") {})) {
			val data = it.arguments?.getString("data")?:""
			val entity = GsonUtils.fromJson(data, ImageInfoEntity::class.java)
			DetailsPage(navHostController = navController, entity)
		}

		composable(Router.album){
			AlbumPage(navHostController = navController)
		}

		composable("${Router.photoPage}/{albumName}",arguments = listOf(navArgument("albumName") {})){
			val data = it.arguments?.getString("albumName")?:"_allImgs"
			PhotoPage(navHostController = navController, albumName = data)
		}
	}
}