package com.fansan.exiffix.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.elevatedButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fansan.exiffix.ui.widgets.SpacerH

/**
 *@author  fansan
 *@version 2023/1/3
 */

@Composable
fun TipDialog(
	tips: String,confirmText:String = "确定" , icons: ImageVector = Icons.Default.DoneAll, click: () -> Unit
,showCancel:Boolean = false,cancelClick:() -> Unit = {}) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = Color(0x66000000))
			.clickable(enabled = false, onClick = {}), contentAlignment = Alignment.Center
	) {

		ElevatedCard(
			modifier = Modifier
				.fillMaxWidth(.7f)
				.aspectRatio(4 / 3f)
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 16.dp)
			) {

				Icon(
					painter = rememberVectorPainter(image = icons),
					contentDescription = "icons",
					modifier = Modifier
						.fillMaxSize(.3f)
						.aspectRatio(1f)
						.align(alignment = Alignment.TopCenter)
						.padding(top = 32.dp)
				)

				Text(
					text = tips,
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.align(alignment = Alignment.Center),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodyMedium
				)


				Row(modifier = Modifier.fillMaxWidth().align(alignment = Alignment.BottomCenter)
					.padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceAround) {

					if (showCancel)
						CommonButton(
							content = "取消"
						) {
							cancelClick.invoke()
						}

					CommonButton(
						content = confirmText
					) {
						click.invoke()
					}
				}
			}
		}

	}
}