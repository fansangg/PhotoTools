package com.fansan.picdatemodify.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fansan.picdatemodify.ui.widgets.SpacerH

/**
 *@author  fansan
 *@version 2023/3/25
 */

@Composable
fun ChooseFuncDialog(content: String, funcList: List<String>, chooseCallBack: (Int) -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxWidth(0.65f)
			.wrapContentHeight()
			.background(
				color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium
			)
			.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally
	) {
		SpacerH(height = 12.dp)
		Text(text = content, fontSize = 15.sp)
		SpacerH(height = 24.dp)
		Column(modifier = Modifier.fillMaxWidth()) {
			funcList.forEachIndexed { index, s ->
				ChooseFuncItem(content = s) {
					chooseCallBack.invoke(index)
				}
				if (index != funcList.size - 1)
					Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline, modifier = Modifier
						.fillMaxWidth())
			}
		}
	}
}

@Composable
fun ChooseFuncItem(content: String, chooseCallBack: () -> Unit) {
	Column(modifier = Modifier
		.fillMaxWidth()
		.wrapContentHeight()
		.noRippleClick { chooseCallBack() }, horizontalAlignment = Alignment.CenterHorizontally) {
		SpacerH(height = 12.dp)
		Text(text = content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
		SpacerH(height = 12.dp)
	}
}