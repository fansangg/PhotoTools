package com.fansan.exiffix.ui.pages

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blankj.utilcode.util.GsonUtils
import com.fansan.exiffix.ui.entity.ErrorFile
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
				progress = viewModel.scanProgress,
				modifier = Modifier.size(180.dp),
				strokeWidth = 5.dp
			)

			Text(text = "${viewModel.currentIndex}/${viewModel.totalFileSize}")
		}

		SpacerH(height = 20.dp)
		Text(
			text = if (viewModel.scanProgress < 1) viewModel.currentExecFileName.value else "DONE",
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
			textAlign = TextAlign.Center
		)

		SpacerH(height = 40.dp)
		Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
			ElevatedButton(onClick = {
				val list = arrayListOf<ErrorFile>()
				list.addAll(viewModel.matchFileList)
				navHostController.navigate("CHECK/${Uri.encode(GsonUtils.toJson(list))}") {
					popUpTo("MAIN")
				}
			}, enabled = viewModel.scanProgress >= 1) {
				Text(text = "CHECK(${viewModel.matchFileList.size})")
			}
		}
	}
}