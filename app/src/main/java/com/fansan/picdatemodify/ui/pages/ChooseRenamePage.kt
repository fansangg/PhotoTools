package com.fansan.picdatemodify.ui.pages

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.fansan.picdatemodify.R
import com.fansan.picdatemodify.common.CommonButton
import com.fansan.picdatemodify.common.DialogWrapper
import com.fansan.picdatemodify.common.ModifyFileNameDialog
import com.fansan.picdatemodify.common.TipDialog
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
	val lazyListState = rememberLazyListState()
	val isScrollInProgress = remember {
		derivedStateOf {
			lazyListState.isScrollInProgress
		}
	}
	val selectedCount = remember {
		derivedStateOf {
			vm.photos.count {
				it.selected
			}
		}
	}
	LaunchedEffect(key1 = albumName, block = {
		vm.getPhotos(context, albumName)
	})
	TitleColumn(title = albumName, backClick = { navHostController.popBackStack() }) {
		Box(
			modifier = Modifier.fillMaxSize()
		) {
			LazyColumn(
				state = lazyListState,
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

			androidx.compose.animation.AnimatedVisibility(visible = !isScrollInProgress.value && selectedCount.value > 0,
			                                              modifier = Modifier
				                                              .align(
					                                              alignment = Alignment.BottomCenter
				                                              )
				                                              .padding(bottom = 30.dp),
			                                              enter = fadeIn(),
			                                              exit = fadeOut(),
			                                              content = {
				                                              CommonButton(
					                                              content = "重命名（${selectedCount.value})",
				                                              ) {

				                                              }
			                                              })

		}

		when{
			vm.modifyFileNameState.showDialogState.value -> {
				Dialog(
					onDismissRequest = { vm.modifyFileNameState.dismissDialog() },
					properties = DialogProperties(dismissOnClickOutside = false)
				) {
					ModifyFileNameDialog(confirm = { prefix, format, symbol, useTaken, skip ->
						vm.modifyFileNameState.format = format
						vm.modifyFileNameState.prefix = prefix
						vm.modifyFileNameState.symbol = symbol
						vm.modifyFileNameState.useTaken = useTaken
						vm.modifyFileNameState.skipNoTaken = skip
						vm.modifyFileNameState.showWarning()
						vm.modifyFileNameState.dismissDialog()
					}) {
						vm.modifyFileNameState.dismissDialog()
					}
				}
			}

			vm.modifyFileNameState.showWarningState.value -> {
				val dateSource =
					if (vm.modifyFileNameState.useTaken) "元数据日期" else "修改日期"
				DialogWrapper(dismissOnBackPress = true) {
					TipDialog(tips = "即将开始批量重命名照片名称\n\n修改范围: '${vm.modifyFileNameState.selectedAlbumName}'\n日期来源: '$dateSource'\n\n是否继续执行此操作？",
					          confirmText = "继续执行",
					          showCancel = true,
					          icons = R.mipmap.warning,
					          click = {
						          /*val uriList = vm.getPhotoByAlbumName(
							          context,
							          vm.modifyFileNameState.selectedAlbumName
						          ).map {
							          Uri.parse(it.uri)
						          }
						          val pendingIntent = MediaStore.createWriteRequest(
							          context.contentResolver,
							          uriList
						          )
						          launcher.launch(
							          IntentSenderRequest.Builder(pendingIntent).build()
						          )*/
						          vm.modifyFileNameState.dismissWarning()
					          },
					          cancelClick = {
						          vm.modifyFileNameState.dismissWarning()
					          })
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
					modifier = Modifier
						.fillMaxHeight()
						.fillMaxWidth()
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