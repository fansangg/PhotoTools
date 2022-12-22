package com.fansan.exiffix.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.entity.ErrorFileNavType
import com.fansan.exiffix.ui.pages.CheckListPage
import com.fansan.exiffix.ui.pages.ExplorerPage
import com.fansan.exiffix.ui.pages.MainPage
import com.fansan.exiffix.ui.pages.ScanPage

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
			"CHECK/{list}",
			arguments = listOf(navArgument("list") { type = ErrorFileNavType() })
		) {
			val list = it.arguments?.getParcelableArrayList<ErrorFile>("list")?: arrayListOf()
			CheckListPage(
				navHostController = navController,
				list = list
			)
		}
	}
}