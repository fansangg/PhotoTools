package com.fansan.picdatemodify.ui.pages

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.blankj.utilcode.util.TimeUtils
import com.fansan.picdatemodify.common.EmptyDir
import com.fansan.picdatemodify.ui.viewmodel.ExplorerViewModel
import com.fansan.picdatemodify.ui.widgets.SpacerW
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/21
 */

@Composable
fun ExplorerDesPage(
	navHostController: NavHostController,
	path: String,
	parent: String,
	confirmClick: () -> Unit
) {
	DirsList(navHostController = navHostController, path = Uri.decode(path), parent, confirmClick)
}

@Composable
fun DirsList(
	navHostController: NavHostController,
	path: String,
	parent: String,
	confirmClick: () -> Unit
) {
	val viewModel = viewModel<ExplorerViewModel>(LocalContext.current as ComponentActivity)
	val fileList = remember {
		mutableStateListOf<File>()
	}

	DisposableEffect(key1 = Unit, effect = {
		fileList.addAll(viewModel.getFiles(path))
		onDispose { }
	})

	Box(modifier = Modifier.fillMaxSize()) {

		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			contentPadding = PaddingValues(top = 10.dp, bottom = if (fileList.isNotEmpty() && !viewModel.isRoot(path)) 90.dp else 10.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			if (!viewModel.isRoot(path)) {
				item {
					ParentCard(parentPath = Uri.decode(parent)) {
						navHostController.popBackStack()
					}
				}
			}

			items(fileList) {
				FileItemCard(file = it) {
					if (it.isDirectory) {
						navHostController.navigate(
							"FILE/${Uri.encode(it.absolutePath)}/${
								Uri.encode(
									path
								)
							}"
						)
					}
				}
			}

			if (fileList.isEmpty()) {
				item {
					EmptyDir(
						modifier = Modifier
							.fillMaxWidth()
							.fillParentMaxHeight(0.8f)
					)
				}
			}

		}

		if (fileList.isNotEmpty() && !viewModel.isRoot(path)) {
			ElevatedButton(modifier = Modifier
				.align(alignment = Alignment.BottomCenter)
				.padding(bottom = 20.dp), onClick = {
				viewModel.confirmPath.tryEmit(path)
				confirmClick.invoke()
			}) {
				Text(text = "Select This Folder")
			}
		}
	}

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentCard(parentPath: String, itemClick: () -> Unit) {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.height(50.dp)
			.padding(horizontal = 16.dp),
		onClick = itemClick
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
				contentDescription = "back"
			)
			SpacerW(width = 12.dp)
			Text(text = parentPath, maxLines = 1, overflow = TextOverflow.Ellipsis)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileItemCard(file: File, itemClick: () -> Unit) {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.height(70.dp)
			.padding(horizontal = 16.dp),
		onClick = itemClick
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				painter = rememberVectorPainter(image = if (file.isDirectory) Icons.Default.ThumbUp else Icons.Default.ThumbUp),
				contentDescription = "back"
			)
			SpacerW(width = 12.dp)
			Column(verticalArrangement = Arrangement.SpaceBetween) {
				Text(
					text = file.name,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					color = MaterialTheme.colorScheme.onSurface
				)
				Text(
					text = TimeUtils.millis2String(file.lastModified()),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					color = MaterialTheme.colorScheme.onSecondaryContainer
				)
			}

		}
	}
}