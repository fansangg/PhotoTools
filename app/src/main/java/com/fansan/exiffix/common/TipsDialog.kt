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
import com.fansan.exiffix.ui.widgets.SpacerW

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
				.wrapContentHeight()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth().wrapContentHeight()
					.padding(horizontal = 16.dp)
			) {

				Icon(
					painter = rememberVectorPainter(image = icons),
					contentDescription = "icons",
					modifier = Modifier
						.fillMaxWidth(.3f)
						.aspectRatio(1f)
						.align(alignment = Alignment.CenterHorizontally)
						.padding(top = 32.dp)
				)

				Text(
					text = tips,
					modifier = Modifier
						.padding(horizontal = 12.dp, vertical = 16.dp)
						.align(alignment = Alignment.CenterHorizontally),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodyMedium
				)


				Row(modifier = Modifier
					.fillMaxWidth()
					.align(alignment = Alignment.CenterHorizontally)
					.padding(bottom = 12.dp), horizontalArrangement = Arrangement.Center) {

					if (showCancel) {
						CommonButton(
							content = "取消", modifier = Modifier.weight(.5f)
						) {
							cancelClick.invoke()
						}
						SpacerW(width = 12.dp)
					}

					CommonButton(
						content = confirmText,modifier = Modifier.weight(.5f)
					) {
						click.invoke()
					}
				}
			}
		}

	}
}