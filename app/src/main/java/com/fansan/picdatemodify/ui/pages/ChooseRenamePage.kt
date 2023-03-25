package com.fansan.picdatemodify.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.fansan.picdatemodify.entity.ImageInfoEntity
import com.fansan.picdatemodify.ui.viewmodel.ChooseRenameViewModel
import com.fansan.picdatemodify.ui.widgets.SpacerW
import com.fansan.picdatemodify.ui.widgets.TitleColumn
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.placeholder

/**
 *@author  fansan
 *@version 2023/3/25
 */

@Composable
fun ChooseRenamePage(navHostController: NavHostController, albumName: String) {
	val vm = viewModel<ChooseRenameViewModel>()
	val context = LocalContext.current
	LaunchedEffect(key1 = albumName, block = {
		vm.getPhotos(context, albumName)
	})
	TitleColumn(title = albumName, backClick = { navHostController.popBackStack() }) {
		Box(
			modifier = Modifier.fillMaxSize()
		) {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.placeholder(
						visible = vm.queryDone.not(),
						highlight = PlaceholderHighlight.fade(),
						color = Color.Gray,
						shape = RoundedCornerShape(4.dp)
					),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				contentPadding = PaddingValues(top = 12.dp)
			) {
				itemsIndexed(vm.photos) { index, item ->
					ChooseRenameItem(entity = item) {
						val isSelected = item.selected
						vm.photos[index] = item.copy(selected = !isSelected)
					}
				}
			}

		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseRenameItem(entity: ImageInfoEntity, onClick: () -> Unit) {
	ElevatedCard(modifier = Modifier.padding(horizontal = 12.dp), onClick = onClick) {
		Box(modifier = Modifier
			.fillMaxWidth()
			.height(IntrinsicSize.Max)) {
			Row(
				modifier = Modifier
					.fillMaxSize()
					.padding(horizontal = 12.dp, vertical = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				AsyncImage(
					model = entity.path,
					contentDescription = "img",
					modifier = Modifier
						.size(60.dp)
						.clip(
							RoundedCornerShape(9.dp)
						),
					contentScale = ContentScale.Crop
				)

				SpacerW(width = 12.dp)

				Text(text = entity.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis)
			}

			if (entity.selected) {
				Box(
					modifier = Modifier.fillMaxHeight().fillMaxWidth()
						.background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
					contentAlignment = Alignment.Center
				) {
					Icon(
						painter = rememberVectorPainter(Icons.Default.Check),
						contentDescription = ""
					)
				}
			}
		}
	}
}