package com.fansan.exiffix.ui.pages

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.*
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.blankj.utilcode.util.ReflectUtils
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.common.*
import com.fansan.exiffix.entity.AlbumType
import com.fansan.exiffix.entity.NewAlbumEntity
import com.fansan.exiffix.router.Router
import com.fansan.exiffix.ui.state.ModifyFileNameState
import com.fansan.exiffix.ui.viewmodel.AlbumViewModel
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import com.fansan.exiffix.util.rememberMutableStateOf
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

	val modifyFileNameState = if (type == AlbumType.FILENAME.name) remember {
		ModifyFileNameState()
	} else null

	val readPermissionState = rememberMultiplePermissionsState(
		permissions = if (Build.VERSION.SDK_INT >= 33) listOf(
			Manifest.permission.READ_MEDIA_IMAGES,
			Manifest.permission.READ_MEDIA_VIDEO,
			Manifest.permission.ACCESS_MEDIA_LOCATION
		) else listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
	)
	val viewModel = viewModel<AlbumViewModel>()

	if (readPermissionState.allPermissionsGranted) {
		viewModel.getAlbums(context)
	}

	TitleColumn(title = "相册", backClick = { navHostController.popBackStack() }) {
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
											modifyFileNameState?.showDialog()
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
											modifyFileNameState?.showDialog()
										}
										else -> {}
									}

								}
							}
						}

						modifyFileNameState?.let {
							if (it.showDialogState.value) {
								Dialog(
									onDismissRequest = { it.dismissDialog() },
									properties = DialogProperties(dismissOnClickOutside = false)
								) {
									ModifyFileNameDialog(confirm = { prefix, format ->

									}) {
										it.dismissDialog()
									}
								}
							}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyFileNameDialog(confirm: (String, String) -> Unit, cancel: () -> Unit) {
	val sampleTime = remember {
		mutableStateOf(System.currentTimeMillis())
	}
	val focusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current
	var prefix by rememberMutableStateOf(value = "IMG")
	var formatSelectorShow by rememberMutableStateOf(value = false)
	var prefixError by rememberMutableStateOf(value = false)
	var timeFormat by rememberMutableStateOf(value = "yyyyMMddHHmmss")
	LaunchedEffect(key1 = prefix, block = {
		prefixError = if (prefix.isNotEmpty()) !ReflectUtils.reflect("android.os.FileUtils")
			.method("isValidExtFilename", prefix).get<Boolean>()
		else false
	})
	var formatOffset by rememberMutableStateOf(value = 0f)
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.background(
				color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium
			)
			.padding(all = 12.dp), horizontalAlignment = Alignment.CenterHorizontally
	) {

		Text(text = "批量文件名修改", fontSize = 16.sp)
		SpacerH(height = 12.dp)
		Text(
			text = "示例：${if (prefix.isNotEmpty()) "${prefix}_" else ""}${
				TimeUtils.millis2String(
					sampleTime.value, timeFormat
				)
			}.png", fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
		)
		SpacerH(height = 12.dp)
		Box {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				OutlinedTextField(value = prefix,
				                  onValueChange = {
					                  prefix = it
				                  },
				                  isError = prefixError,
				                  singleLine = true,
				                  label = {
					                  if (prefixError) {
						                  Text(text = "非法字符", fontSize = 8.sp)
					                  } else {
						                  Text(text = "前缀", fontSize = 8.sp)
					                  }
				                  },

				                  modifier = Modifier
					                  .weight(.28f)
					                  .focusRequester(focusRequester = focusRequester),
				                  colors = TextFieldDefaults.outlinedTextFieldColors(
					                  focusedBorderColor = MaterialTheme.colorScheme.outline,
					                  focusedLabelColor = MaterialTheme.colorScheme.outline,
					                  unfocusedBorderColor = MaterialTheme.colorScheme.outline,
					                  unfocusedLabelColor = MaterialTheme.colorScheme.outline
				                  )
				)

				SpacerW(width = 12.dp)

				OutlinedTextField(value = timeFormat,
				                  onValueChange = {
					                  timeFormat = it
				                  },
				                  modifier = Modifier
					                  .weight(.72f)
					                  .noRippleClick {
						                  formatSelectorShow = true
						                  focusManager.clearFocus()
					                  }
					                  .onGloballyPositioned {
						                  formatOffset = it.positionInParent().x
					                  },
				                  enabled = false,
				                  singleLine = true,
				                  label = {
					                  Text(text = "日期格式", fontSize = 8.sp)
				                  },
				                  colors = TextFieldDefaults.outlinedTextFieldColors(
					                  disabledBorderColor = MaterialTheme.colorScheme.outline,
					                  disabledLabelColor = MaterialTheme.colorScheme.outline,
					                  disabledTextColor = MaterialTheme.colorScheme.onSurface
				                  ),
				                  textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
				)
			}


			DropdownMenu(expanded = formatSelectorShow,
			             onDismissRequest = { formatSelectorShow = false },
			             offset = DpOffset(
				             x = with(LocalDensity.current) { formatOffset.toDp() },
				             y = 0.dp
			             )
			) {
				DropdownMenuItem(text = { Text(text = "yyyyMMddHHmmss") }, onClick = {
					timeFormat = "yyyyMMddHHmmss"
					formatSelectorShow = false
				}, trailingIcon = {
					if (timeFormat == "yyyyMMddHHmmss") {
						Icon(
							painter = rememberVectorPainter(image = Icons.Default.Check),
							contentDescription = "check"
						)
					}
				})

				DropdownMenuItem(text = { Text(text = "yyyyMMdd_HHmmss") }, onClick = {
					timeFormat = "yyyyMMdd_HHmmss"
					formatSelectorShow = false
				}, trailingIcon = {
					if (timeFormat == "yyyyMMdd_HHmmss") {
						Icon(
							painter = rememberVectorPainter(image = Icons.Default.Check),
							contentDescription = "check"
						)
					}
				})

				DropdownMenuItem(text = { Text(text = "yyyyMMdd-HHmmss") }, onClick = {
					timeFormat = "yyyyMMdd-HHmmss"
					formatSelectorShow = false
				}, trailingIcon = {
					if (timeFormat == "yyyyMMdd-HHmmss") {
						Icon(
							painter = rememberVectorPainter(image = Icons.Default.Check),
							contentDescription = "check"
						)
					}
				})
			}
		}

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 24.dp),
			horizontalArrangement = Arrangement.Center
		) {


			CommonButton(
				content = "取消", modifier = Modifier.weight(.5f)
			) {
				cancel.invoke()
			}
			SpacerW(width = 12.dp)

			CommonButton(
				content = "确定", modifier = Modifier.weight(.5f)
			) {
				confirm.invoke(prefix, timeFormat)
			}
		}
	}
}