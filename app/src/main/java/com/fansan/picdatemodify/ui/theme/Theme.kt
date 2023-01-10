package com.fansan.picdatemodify.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.blankj.utilcode.util.ToastUtils

private val DarkColorScheme = darkColorScheme(
	primary = Black24,
	onPrimary = White850,
	secondary = Gray,
	onSecondary = White850,
	surface = Black24,
	onSurface = White850,
	background = Black11,
	onBackground = White850,
)

private val LightColorScheme = lightColorScheme(
	primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40

	/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ExifFIXTheme(
	darkTheme: Boolean = isSystemInDarkTheme(), // Dynamic color is available on Android 12+
	dynamicColor: Boolean = false, content: @Composable () -> Unit
) {

	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}

	MaterialTheme(
		colorScheme = colorScheme, typography = Typography, content = content
	)
}