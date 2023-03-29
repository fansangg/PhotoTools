package com.fansan.picdatemodify.ui.pages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fansan.picdatemodify.R
import com.fansan.picdatemodify.common.logd
import com.fansan.picdatemodify.ui.theme.splashFont
import com.fansan.picdatemodify.ui.widgets.SpacerH
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

	val animState = remember {
		mutableStateOf(false)
	}

	val statusBarColor = MaterialTheme.colorScheme.background
	val systemUiController = rememberSystemUiController()
	DisposableEffect(key1 = systemUiController, effect = {
		systemUiController.setStatusBarColor(statusBarColor)
		onDispose {  }
	})

	androidx.activity.compose.BackHandler(true) {

	}

	val alphaAnim = animateFloatAsState(
		targetValue = if (animState.value) 1f else 0f,
		animationSpec = tween(durationMillis = 2000)
	)
	val offsetAnim = animateDpAsState(
		targetValue = if (animState.value) 0.dp else 100.dp, animationSpec = tween(1000)
	)

	Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

		Image(
			painter = painterResource(id = R.mipmap.ic_launcher),
			contentDescription = "logo",
			modifier = Modifier
				.size(120.dp)
				.alpha(alphaAnim.value)
				.clip(RoundedCornerShape(24.dp)),
			contentScale = ContentScale.Crop
		)

		SpacerH(height = 12.dp)

		Text(
			text = "照片小工具",
			fontFamily = splashFont,
			fontSize = 26.sp,
			modifier = Modifier
				.alpha(alphaAnim.value)
				.offset(0.dp, offsetAnim.value),
			fontWeight = FontWeight.SemiBold
		)

		LaunchedEffect(key1 = null) {
			animState.value = true
			delay(2500)
			navController.navigate(route = "MAIN")
		}
	}

}