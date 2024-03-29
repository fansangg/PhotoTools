package com.fansan.picdatemodify.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blankj.utilcode.util.GsonUtils
import com.fansan.picdatemodify.entity.*
import com.fansan.picdatemodify.router.Router
import com.fansan.picdatemodify.ui.pages.*

/**
 *@author  fansan
 *@version 2022/12/20
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PhotoToolsNavHost(
	modifier: Modifier,
	navController: NavHostController = rememberNavController ()
) {

	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = "splash"
	) {
		composable("splash") {
			SplashScreen(navController = navController)
		}

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

		defaultAnimComposable("${Router.details}/{data}", arguments = listOf(navArgument("data") {})) {
			val data = it.arguments?.getString("data") ?: ""
			val entity = GsonUtils.fromJson(data, ImageInfoEntity::class.java)
			DetailsPage(navHostController = navController, entity)
		}

		defaultAnimComposable(
			"${Router.album}/{type}",
			arguments = listOf(navArgument("type") {})) {
			val type = it.arguments?.getString("type") ?: AlbumType.DATE.name
			AlbumPage(navHostController = navController, type)
		}

		defaultAnimComposable(
			"${Router.photoPage}/{albumName}",
			arguments = listOf(navArgument("albumName") {})
		) {
			val data = it.arguments?.getString("albumName") ?: "_allImgs"
			PhotoPage(navHostController = navController, albumName = data)
		}

		defaultAnimComposable("${Router.exifInfo}/{data}", arguments = listOf(navArgument("data") {})){
			val data = it.arguments?.getString("data") ?: ""
			val entity = GsonUtils.fromJson(data, ImageInfoEntity::class.java)
			ExifDetailsPage(navHostController = navController, info = entity)
		}

		defaultAnimComposable("${Router.chooseRename}/{albumName}", arguments = listOf(navArgument("albumName"){})){
			val data = it.arguments?.getString("albumName") ?: "_allImgs"
			ChooseRenamePage(navHostController = navController, albumName = data)
		}

		defaultAnimComposable("${Router.exifPhotoList}/{albumName}",listOf(navArgument("albumName"){})){
			val data = it.arguments?.getString("albumName") ?: "_allImgs"
			ExifPhotoPage(navHostController = navController, albumName = data)
		}
	}
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.defaultAnimComposable(
	route: String,
	arguments: List<NamedNavArgument> = emptyList(),
	content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {

	composable(route = route, arguments = arguments, content = content,
	           enterTransition = {
		           slideIntoContainer(
			           AnimatedContentTransitionScope
			           .SlideDirection.Left,
			           tween(durationMillis = 500)
		           )
	           },
	           exitTransition = {
		           slideOutOfContainer(
			           AnimatedContentTransitionScope.SlideDirection.Left,
			           tween(durationMillis = 500)
		           )
	           },
	           popExitTransition = {
		           slideOutOfContainer(
			           AnimatedContentTransitionScope.SlideDirection.Right,
			           tween(durationMillis = 500)
		           )
	           },
	           popEnterTransition = {
		           slideIntoContainer(
			           AnimatedContentTransitionScope.SlideDirection.Right,
			           tween(durationMillis = 500)
		           )
	           })
}