package com.fansan.picdatemodify.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.blankj.utilcode.util.GsonUtils
import com.fansan.picdatemodify.entity.*
import com.fansan.picdatemodify.router.Router
import com.fansan.picdatemodify.ui.pages.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

/**
 *@author  fansan
 *@version 2022/12/20
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PhotoToolsNavHost(
	modifier: Modifier,
	navController: NavHostController = rememberAnimatedNavController()
) {

	AnimatedNavHost(
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
			           AnimatedContentScope.SlideDirection.Left,
			           tween(durationMillis = 500)
		           )
	           },
	           exitTransition = {
		           slideOutOfContainer(
			           AnimatedContentScope.SlideDirection.Left,
			           tween(durationMillis = 500)
		           )
	           },
	           popExitTransition = {
		           slideOutOfContainer(
			           AnimatedContentScope.SlideDirection.Right,
			           tween(durationMillis = 500)
		           )
	           },
	           popEnterTransition = {
		           slideIntoContainer(
			           AnimatedContentScope.SlideDirection.Right,
			           tween(durationMillis = 500)
		           )
	           })
}