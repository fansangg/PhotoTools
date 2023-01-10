package com.fansan.picdatemodify.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.*
import com.fansan.picdatemodify.R
import com.fansan.picdatemodify.ui.widgets.SpacerH

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
	ElevatedCard(modifier = Modifier.size(150.dp)) {
		val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(json))
		val lottieState by animateLottieCompositionAsState(
			composition = composition, iterations = LottieConstants.IterateForever, isPlaying = true
		)
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Column(horizontalAlignment = Alignment.CenterHorizontally) {
				LottieAnimation(
					composition = composition,
					progress = { lottieState },
					modifier = Modifier.size(80.dp)
				)

				Text(
					text = content,
					textAlign = TextAlign.Center,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
				)
			}
		}
	}
}

@Composable
fun FixLoading(
	isDone: Boolean,
	content: String,
	successCount: Int,
	errorCount: Int,
	skipCount: Int = 0,
	confirmClick: () -> Unit
) {
	val json = if (isSystemInDarkTheme()) "anim/Chicken_dark.json" else "anim/Chicken.json"
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth(0.65f)
			.heightIn(
				min = Dp.Unspecified, max = (LocalConfiguration.current.screenHeightDp * 0.6).dp
			)
	) {
		val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(json))
		val lottieState by animateLottieCompositionAsState(
			composition = composition,
			iterations = LottieConstants.IterateForever,
			isPlaying = !isDone
		)
		Column(
			modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f, fill = false), contentAlignment = Alignment.Center
			) {

				Crossfade(targetState = isDone) {
					if (it) {
						Column(
							modifier = Modifier.fillMaxWidth(),
							horizontalAlignment = Alignment.CenterHorizontally,
							verticalArrangement = Arrangement.SpaceAround
						) {
							Icon(
								painter = painterResource(id = R.mipmap.done_all),
								contentDescription = "done",
								modifier = Modifier.size(80.dp)
							)
							SpacerH(height = 12.dp)
							Text(text = buildAnnotatedString {
								append("成功：")
								withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
									append(successCount.toString())
								}
								append("\n失败：")
								withStyle(
									SpanStyle(
										color = Color.Red.copy(alpha = .9f),
										fontWeight = FontWeight.SemiBold
									)
								) {
									append("$errorCount")
								}
								if (skipCount > 0) {
									append("\n跳过：$skipCount")
								}
							})
						}
					} else {
						Box(
							modifier = Modifier.fillMaxWidth(),
							contentAlignment = Alignment.Center
						) {
							LottieAnimation(
								composition = composition,
								progress = { lottieState },
								modifier = Modifier.size(100.dp)
							)
						}
					}
				}

			}

			if (isDone) {
				CommonButton(content = "确定", modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
					confirmClick.invoke()
				}
			} else {
				Text(
					text = content,
					textAlign = TextAlign.Center,
					overflow = TextOverflow.Ellipsis,
					maxLines = 2,
					modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
				)
			}
		}
	}
}