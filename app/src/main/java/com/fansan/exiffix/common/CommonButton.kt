package com.fansan.exiffix.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun CommonButton(content:String,modifier: Modifier = Modifier,click:() -> Unit){
	ElevatedButton(
		onClick = { click.invoke() },
		modifier = modifier,
		contentPadding = PaddingValues(horizontal = 32.dp, vertical = 8.dp),
		colors = ButtonDefaults.elevatedButtonColors(
			containerColor = Color(0xff3056f4),
			contentColor = Color.White
		)
	) {
		Text(text = content)
	}
}