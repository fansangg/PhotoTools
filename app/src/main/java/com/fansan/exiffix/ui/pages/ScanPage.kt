package com.fansan.exiffix.ui.pages

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fansan.exiffix.ui.viewmodel.ScanViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.TitleColumn

/**
 *@author  fansan
 *@version 2022/12/21
 */

@SuppressLint("FileEndsWithExt")
@Composable
fun ScanPage(navHostController: NavHostController, path: String) {
	val selectedPath = Uri.decode(path)
	val viewModel = viewModel<ScanViewModel>()

	LaunchedEffect(key1 = Unit, block = {
		viewModel.scanFiles(selectedPath)
	})

	TitleColumn(title = "Scan", backClick = { navHostController.popBackStack() }) {

		SpacerH(height = 20.dp)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentHeight(),
			contentAlignment = Alignment.Center
		) {

			CircularProgressIndicator(
				progress = viewModel.scanProgress, modifier = Modifier.size(180.dp), strokeWidth = 5.dp
			)
			Column(modifier = Modifier.size(180.dp), horizontalAlignment = Alignment.CenterHorizontally) {
				Text(
					text = if (viewModel.scanProgress < 1) viewModel.currentExecFileName.value else "DONE",
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.padding(horizontal = 12.dp)
				)
				SpacerH(height = 10.dp)
				Text(text = "${viewModel.matchFileList.size}/${viewModel.totalFileSize}")
			}
		}
	}
}