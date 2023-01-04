package com.fansan.exiffix.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.fansan.exiffix.ui.widgets.SpacerH

/**
 *@author  fansan
 *@version 2023/1/4
 */

@Composable
fun EmptyDir(modifier: Modifier, tips:String = "Empty Folder") {
	val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("anim/404.json"))
	val lottieState by animateLottieCompositionAsState(
		composition = composition,
		iterations = LottieConstants.IterateForever,
		isPlaying = true
	)
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		LottieAnimation(composition = composition, progress = { lottieState },modifier = Modifier.size(150.dp))
		/*Icon(
			painter = rememberVectorPainter(image = Icons.Default.PsychologyAlt),
			contentDescription = "empty",
			modifier = Modifier.size(60.dp)
		)*/
		SpacerH(height = 8.dp)
		Text(text = tips, style = MaterialTheme.typography.titleLarge)
	}
}