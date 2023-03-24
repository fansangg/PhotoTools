package com.fansan.picdatemodify.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fansan.picdatemodify.ui.theme.Black11


@Composable
fun CommonButton(content:String,modifier: Modifier = Modifier,enable:Boolean = true,click:() -> Unit){
	Button(
		onClick = { click.invoke() },
		modifier = modifier,
		contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isSystemInDarkTheme()) Color(0xff3056f4) else Black11,
			contentColor = Color.White
		),
		enabled = enable
	) {
		Text(text = content)
	}
}