package com.fansan.exiffix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fansan.exiffix.ui.ExifFIXNavHost
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.theme.ExifFIXTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			ExifFIXTheme { // A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					ExifFIXNavHost(modifier = Modifier.fillMaxSize())
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
	ExifFIXTheme {
		Greeting("Android")
	}
}