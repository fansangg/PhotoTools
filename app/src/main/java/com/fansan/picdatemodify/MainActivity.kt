package com.fansan.picdatemodify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fansan.picdatemodify.ui.PhotoToolsNavHost
import com.fansan.picdatemodify.ui.theme.PhotoToolsTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {

			val statusBarColor = MaterialTheme.colorScheme.primary
			val systemUiController = rememberSystemUiController()
			LaunchedEffect(key1 = systemUiController, block = {
				systemUiController.setStatusBarColor(statusBarColor)
			})

			PhotoToolsTheme { // A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					PhotoToolsNavHost(modifier = Modifier.fillMaxSize())
				}
			}
		}
	}
}

@Composable
fun Greeting(name: String) {
	Card(modifier = Modifier.size(100.dp)) {
		Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
			Text(text = "Hello $name!")
		}
	}
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	PhotoToolsTheme {
		Greeting("Android")
	}
}