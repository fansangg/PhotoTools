package com.fansan.exiffix.ui.widgets

import android.icu.text.CaseMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansan.exiffix.common.noRippleClick

/**
 *@author  fansan
 *@version 2022/12/20
 */

@Composable
fun CommonTitle(title: String, modifier: Modifier = Modifier, withBackIcon:Boolean = true,backClick: () -> Unit) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.height(60.dp)
			.background(color = MaterialTheme.colorScheme.primary)
	) {
		if (withBackIcon)
			Box(modifier = Modifier
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
					colorFilter = if (isSystemInDarkTheme()) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.White)
				)
			}

		Text(
			text = title,
			color = MaterialTheme.colorScheme.onPrimary,
			fontSize = 18.sp,
			fontWeight = FontWeight.SemiBold,
			modifier = Modifier.align(alignment = Alignment.Center)
		)
	}
}