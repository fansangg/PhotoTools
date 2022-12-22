package com.fansan.exiffix.ui.pages

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fansan.exiffix.ui.viewmodel.ExplorerViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn

/**
 *@author  fansan
 *@version 2022/12/20
 */

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainPage(navHostController: NavHostController) {
	val viewModel = viewModel<ExplorerViewModel>(LocalContext.current as ComponentActivity)
	val confirmPath = viewModel.confirmPath.collectAsStateWithLifecycle()
	BackHandler()
	TitleColumn(title = "HOME", withBackIcon = false, backClick = { }) {
		SpacerH(height = 20.dp)
		Text(
			text = confirmPath.value.ifEmpty { "SELECTED PICTURE FOLDER" },
			fontSize = 18.sp,
			fontWeight = FontWeight.SemiBold,
			modifier = Modifier.padding(start = 12.dp)
		)
		SpacerH(height = 30.dp)
		Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

			ElevatedButton(onClick = { navHostController.navigate("EXPLORER") }) {
				Text(text = "OPEN EXPLORER")
			}
		}

		if (confirmPath.value.isNotEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 40.dp),
				contentAlignment = Alignment.Center
			) {
				ElevatedButton(onClick = { navHostController.navigate("SCAN/${Uri.encode(confirmPath.value)}") }) {
					Text(text = "SCAN")
				}
			}
		}

	}

}

@Composable
fun BackHandler(){
	var exitTime = 0L
	val context = LocalContext.current
	BackHandler {
		if (System.currentTimeMillis() - exitTime > 2000){
			Toast.makeText(context,"再按一次退出",Toast.LENGTH_SHORT).show()
			exitTime = System.currentTimeMillis()
		}else{
			(context as Activity).finish()
		}
	}
}