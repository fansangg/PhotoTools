package com.fansan.exiffix.common

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.fansan.exiffix.ui.widgets.SpacerH

/**
 *@author  fansan
 *@version 2023/1/3
 */

@Composable
fun LoadingStyle1() {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = Color(0x66000000)),
		contentAlignment = Alignment.Center
	) {
		ElevatedCard(modifier = Modifier.size(150.dp)) {
			val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("anim/Idea.json"))
			val lottieState by animateLottieCompositionAsState(
				composition = composition,
				iterations = LottieConstants.IterateForever,
				isPlaying = true
			)
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					LottieAnimation(
						composition = composition,
						progress = { lottieState },
						modifier = Modifier.size(80.dp)
					)
					SpacerH(height = 12.dp)

					Text(text = "正在加载中...")
				}
			}
		}
	}
}

@Composable
fun LoadingStyle2(content: String = "正在加载中...") {
	val json = if (isSystemInDarkTheme()) "anim/Chicken_dark.json" else "anim/Chicken.json"
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = Color(0x66000000)),
		contentAlignment = Alignment.Center
	) {
		ElevatedCard(modifier = Modifier.size(150.dp)) {
			val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(json))
			val lottieState by animateLottieCompositionAsState(
				composition = composition,
				iterations = LottieConstants.IterateForever,
				isPlaying = true
			)
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					LottieAnimation(
						composition = composition,
						progress = { lottieState },
						modifier = Modifier.size(80.dp)
					)

					Text(text = content, textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp))
				}
			}
		}
	}
}