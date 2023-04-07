package com.fansan.picdatemodify.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansan.picdatemodify.common.noRippleClick
import com.fansan.picdatemodify.ui.theme.White850

/**
 *@author  fansan
 *@version 2022/12/20
 */

@Composable
fun CommonTitle(
	title: String,
	modifier: Modifier = Modifier,
	withBackIcon: Boolean = true,
	rightWidget: @Composable (BoxScope.() -> Unit)? = null,
	rightClick: (() -> Unit)? = null,
	backClick: () -> Unit
) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.height(60.dp)
			.background(color = MaterialTheme.colorScheme.primary)
	) {
		if (withBackIcon) Box(modifier = Modifier
			.align(alignment = Alignment.CenterStart)
			.fillMaxHeight()
			.wrapContentWidth()
			.noRippleClick {
				backClick.invoke()
			}) {

			Image(
				painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
				contentDescription = "BackIcon",
				modifier = Modifier
					.align(alignment = Alignment.Center)
					.padding(horizontal = 16.dp),
				colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) White850 else Color.White)
			)
		}

		if (rightWidget != null) {
			Box(
				modifier = Modifier
					.align(alignment = Alignment.CenterEnd)
					.padding(end = 12.dp)
					.noRippleClick {
						rightClick?.invoke()
					}
			) {
				rightWidget()
			}
		}

		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge.copy(
				color = if (isSystemInDarkTheme()) White850 else Color.White,
				fontSize = 18.sp
			),
			modifier = Modifier.align(alignment = Alignment.Center)
		)
	}
}