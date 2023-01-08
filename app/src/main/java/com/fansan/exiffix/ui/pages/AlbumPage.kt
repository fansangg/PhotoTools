package com.fansan.exiffix.ui.pages

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.*
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.fansan.exiffix.common.*
import com.fansan.exiffix.entity.AlbumType
import com.fansan.exiffix.entity.NewAlbumEntity
import com.fansan.exiffix.router.Router
import com.fansan.exiffix.ui.state.ModifiedFileTaskState
import com.fansan.exiffix.ui.viewmodel.AlbumViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 *@author  fansan
 *@version 2022/12/26
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlbumPage(navHostController: NavHostController, type: String) {
	val context = LocalContext.current

	val viewModel = viewModel<AlbumViewModel>()

	val readPermissionState = rememberMultiplePermissionsState(
		permissions = if (Build.VERSION.SDK_INT >= 33) listOf(
			Manifest.permission.READ_MEDIA_IMAGES,
			Manifest.permission.READ_MEDIA_VIDEO,
			Manifest.permission.ACCESS_MEDIA_LOCATION
		) else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
	)

	if (readPermissionState.allPermissionsGranted) {
		viewModel.getAlbums(context)
	}

	val title = when (type) {
		AlbumType.DATE.name -> "选择相册来查看"
		AlbumType.FILENAME.name -> "选择要操作的相册"
		else -> "相册"
	}

	val launcher = writeRequest() {
		viewModel.modifyFileNameState.taskInProgress()
		viewModel.modifiedFileNames(context)
		viewModel.modifyFileNameState.dismissWarning()
	}

	TitleColumn(title = title, backClick = { navHostController.popBackStack() }) {
		if (readPermissionState.allPermissionsGranted) {
			if (viewModel.allDone.value) {
				if (viewModel.newAlbumMap.isEmpty()) {
					EmptyDir(modifier = Modifier.fillMaxSize(), tips = "没有找到文件")
				} else {
					Box {
						LazyColumn(
							modifier = Modifier.fillMaxSize(),
							contentPadding = PaddingValues(vertical = 12.dp),
							verticalArrangement = Arrangement.spacedBy(12.dp)
						) {
							item {
								val allPhotoEntity = NewAlbumEntity(
									"所有照片", viewModel.firstImg, viewModel.allImageCount
								)
								AlbumCard(albumEntity = allPhotoEntity) {
									when (type) {
										AlbumType.DATE.name -> {
											navHostController.navigate("${Router.photoPage}/_allImgs")
										}
										AlbumType.FILENAME.name -> {
											viewModel.modifyFileNameState.selectedAlbumName = "all"
											viewModel.modifyFileNameState.showDialog()
										}
										else -> {}
									}

								}
							}
							items(viewModel.newAlbumMap.keys.toList()) {
								val entity = viewModel.newAlbumMap.getValue(it)
								AlbumCard(albumEntity = entity) {
									when (type) {
										AlbumType.DATE.name -> {
											navHostController.navigate("${Router.photoPage}/${entity.albumName}")
										}
										AlbumType.FILENAME.name -> {
											viewModel.modifyFileNameState.selectedAlbumName =
												entity.albumName
											viewModel.modifyFileNameState.showDialog()
										}
										else -> {}
									}

								}
							}
						}

						when {
							viewModel.modifyFileNameState.showDialogState.value -> {
								Dialog(
									onDismissRequest = { viewModel.modifyFileNameState.dismissDialog() },
									properties = DialogProperties(dismissOnClickOutside = false)
								) {
									ModifyFileNameDialog(confirm = { prefix, format, symbol, useTaken, skip ->
										viewModel.modifyFileNameState.format = format
										viewModel.modifyFileNameState.prefix = prefix
										viewModel.modifyFileNameState.symbol = symbol
										viewModel.modifyFileNameState.useTaken = useTaken
										viewModel.modifyFileNameState.skipNoTaken = skip
										viewModel.modifyFileNameState.showWarning()
										viewModel.modifyFileNameState.dismissDialog()
									}) {
										viewModel.modifyFileNameState.dismissDialog()
									}
								}
							}

							viewModel.modifyFileNameState.showWarningState.value -> {
								val dateSource =
									if (viewModel.modifyFileNameState.useTaken) "元数据日期" else "最后修改日期"
								TipDialog(tips = "即将开始批量重命名照片名称\n\n修改范围: '${viewModel.modifyFileNameState.selectedAlbumName}'\n日期来源: '$dateSource'\n\n是否继续执行此操作？",
								          confirmText = "继续执行",
								          showCancel = true,
								          icons = Icons.Default.Warning,
								          click = {
									          val uriList = viewModel.getPhotoByAlbumName(
										          context,
										          viewModel.modifyFileNameState.selectedAlbumName
									          ).map {
											          Uri.parse(it.uri)
										          }
									          val pendingIntent = createWriteRequest(
										          context.contentResolver,
										          uriList
									          )
									          launcher.launch(
										          IntentSenderRequest.Builder(pendingIntent).build()
									          )
								          },
								          cancelClick = {
									          viewModel.modifyFileNameState.dismissWarning()
								          })
							}
						}

						when (viewModel.modifyFileNameState.modifiedFileNameTaskState.value) {
							is ModifiedFileTaskState.InProgress, is ModifiedFileTaskState.Done -> {
								FixLoading(
									isDone = viewModel.modifyFileNameState.modifiedFileNameTaskState.value is ModifiedFileTaskState.Done,
									content = "${viewModel.modifyFileNameState.modifiedFileNameCurrentIndex.value}/${viewModel.modifyFileNameState.modifiedFileNameListCount.value}",
									successCount = viewModel.modifyFileNameState.modifiedFileNameSuccessCount.value,
									errorCount = viewModel.modifyFileNameState.modifiedFileNameFieldCount.value,
									skipCount = viewModel.modifyFileNameState.modifiedFileNameSkipCount.value
								) {
									viewModel.modifyFileNameState.resetAll()
								}
							}

							else -> {}
						}
					}

				}
			} else {
				LoadingStyle2()
			}
		} else {
			val textToShow = if (readPermissionState.shouldShowRationale) {
				"需要读取照片权限才能正常工作"
			} else {
				"需要读取照片权限才能正常工作\n请允许此权限"
			}

			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset("anim/permission.json"))
				val lottieState by animateLottieCompositionAsState(
					composition = composition, iterations = 1, isPlaying = true
				)
				LottieAnimation(
					composition = composition,
					progress = { lottieState },
					modifier = Modifier.size(160.dp)
				)
				SpacerH(height = 12.dp)
				Text(textToShow, textAlign = TextAlign.Center)
				SpacerH(height = 40.dp)
				CommonButton(content = "申请权限",
				             click = { readPermissionState.launchMultiplePermissionRequest() })
				SpacerH(height = 20.dp)
				CommonButton(content = "前往设置", click = {
					(context as Activity).startActivity(
						Intent(
							Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
							Uri.fromParts("package", context.packageName, null)
						)
					)
				})
			}
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumCard(albumEntity: NewAlbumEntity, click: () -> Unit) {
	ElevatedCard(
		onClick = { click.invoke() }, modifier = Modifier
			.padding(horizontal = 12.dp)
			.fillMaxWidth()
	) {
		Row {
			AsyncImage(
				model = albumEntity.firstImg,
				contentDescription = "Thumbial",
				modifier = Modifier.size(80.dp),
				contentScale = ContentScale.Crop
			)

			SpacerW(width = 12.dp)

			Text(
				text = "${albumEntity.albumName}(${albumEntity.count})",
				modifier = Modifier.align(alignment = Alignment.CenterVertically),
				fontSize = 16.sp,
				fontWeight = FontWeight.SemiBold
			)
		}
	}
}

