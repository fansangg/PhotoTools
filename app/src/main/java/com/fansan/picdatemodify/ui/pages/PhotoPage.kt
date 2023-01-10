package com.fansan.picdatemodify.ui.pages

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.fansan.picdatemodify.R
import com.fansan.picdatemodify.common.*
import com.fansan.picdatemodify.entity.ImageInfoEntity
import com.fansan.picdatemodify.router.Router
import com.fansan.picdatemodify.ui.viewmodel.PhotoPageViewModel
import com.fansan.picdatemodify.ui.widgets.SpacerH
import com.fansan.picdatemodify.ui.widgets.TitleColumn
import com.fansan.picdatemodify.util.rememberMutableStateOf
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 *@author  fansan
 *@version 2022/12/30
 */

@Composable
fun PhotoPage(navHostController: NavHostController, albumName: String) {
	val viewModel = viewModel<PhotoPageViewModel>()

	val tipDialogShow = rememberSaveable {
		mutableStateOf(true)
	}


	val writePermission = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = {
			if (it){
				viewModel.fixAll()
			}else{
				ToastUtils.showShort("请允许权限来同步日期")
			}
		}
	)

	val resultState = navHostController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("path")?.observeAsState()
	resultState?.value?.let {
		viewModel.errorPhotoList.removeIf { entity ->
			entity.path == it
		}
		if (viewModel.errorPhotoList.isEmpty()){
			tipDialogShow.value = true
		}
		navHostController.currentBackStackEntry?.savedStateHandle?.remove<String>("path")
	}

	val context = LocalContext.current
	LaunchedEffect(key1 = Unit, block = {
		if (viewModel.allDone.value.not()) {
			viewModel.getPhotos(context, albumName)
		}
	})

	val inProgress = remember {
		derivedStateOf {
			viewModel.scanProgress > 0 && viewModel.scanProgress < 1f
		}
	}

	val scrollState = rememberLazyGridState()
	val isScrollInProgress by remember {
		derivedStateOf {
			scrollState.isScrollInProgress
		}
	}

	val launcher =
		writeRequest(){
			viewModel.fixAll()
		}

	val showWraningTips = rememberSaveable {
		mutableStateOf(false)
	}


	TitleColumn(title = if (albumName == "_allImgs") "所有照片" else albumName,
	            backClick = { navHostController.popBackStack() }) {
		if (viewModel.allDone.value) {
			Box(modifier = Modifier.fillMaxSize()) {
				LazyVerticalGrid(
					columns = GridCells.Fixed(4),
					modifier = Modifier.fillMaxSize(),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
					contentPadding = PaddingValues(
						start = 12.dp, end = 12.dp, top = 12.dp, bottom = 60.dp
					),
					state = scrollState
				) {
					items(viewModel.errorPhotoList, key = { it.path }) {
						ImageItem(info = it) {
							navHostController.navigate(
								"${Router.details}/${
									Uri.encode(
										GsonUtils.toJson(
											it
										)
									)
								}"
							)
						}
					}
				}

				if (viewModel.errorPhotoList.isNotEmpty()) {

					androidx.compose.animation.AnimatedVisibility(visible = !isScrollInProgress,
					                                              modifier = Modifier
						                                              .align(
							                                              alignment = Alignment.BottomCenter
						                                              )
						                                              .padding(bottom = 30.dp),
					                                              enter = fadeIn(),
					                                              exit = fadeOut(),
					                                              content = {
						                                              CommonButton(
							                                              content = if (viewModel.errorPhotoList.size > 1) "批量修复" else "修复",
						                                              ) {
							                                              showWraningTips.value =
								                                              true
						                                              }
					                                              })
				}

				if (showWraningTips.value) {
					DialogWrapper(dismissOnBackPress = true){
						TipDialog(tips = "当前结果内所有照片修改日期将同步为照片的元数据日期，是否继续执行此操作？",
						          confirmText = "继续执行",
						          showCancel = true,
						          icons = R.mipmap.warning,
						          click = {
							          showWraningTips.value = false
							          if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
								          val result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

								          if (result == PackageManager.PERMISSION_GRANTED){
									          viewModel.fixAll()
								          }else{
									          writePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
								          }
							          }else{
								          val uriList = viewModel.errorPhotoList.map {
									          Uri.parse(it.uri)
								          }
								          val intent = MediaStore.createWriteRequest(
									          context.contentResolver, uriList
								          )
								          launcher.launch(IntentSenderRequest.Builder(intent).build())
							          }
						          },
						          cancelClick = {
							          showWraningTips.value = false
						          })
					}
				}

				if (inProgress.value || viewModel.allFixDone.value) {
					FixLoading(
						isDone = viewModel.allFixDone.value,
						content = "${viewModel.currentIndex}/${viewModel.errorPhotoList.size}\n${viewModel.currentExecFileName}",
						successCount = viewModel.successFileList.size,
						errorCount = viewModel.failedCount
					) {
						viewModel.scanFile(context)
						viewModel.allFixDone.value = false
						tipDialogShow.value = true
					}
				}

				if (tipDialogShow.value) {
					val tips =
						if (viewModel.errorPhotoList.isNotEmpty()) "发现${viewModel.errorPhotoList.size}张与元数据日期不同步的照片" else "所有照片修改日期均已和元数据日期同步"
					val icon =
						if (viewModel.errorPhotoList.isNotEmpty()) R.mipmap.done_all else R.mipmap.thumb_up
					DialogWrapper{
						TipDialog(tips = tips, icons = icon, click = {
							if (viewModel.errorPhotoList.isNotEmpty()) tipDialogShow.value = false
							else navHostController.popBackStack()
						})
					}
				}
			}
		} else {
			LoadingStyle2()
		}
	}

}


@Deprecated("old design")
@Composable
fun AnalysisDialog(
	progress: Float,
	currentIndex: Int,
	total: Int,
	currentFileName: String,
	findSize: Int,
	confrimClick: () -> Unit
) {
	var doneFlag by rememberSaveable {
		mutableStateOf(false)
	}
	if (progress >= 1) {
		doneFlag = true
	}
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color = Color(0x33000000)),
		contentAlignment = Alignment.Center
	) {

		ElevatedCard(
			modifier = Modifier
				.fillMaxWidth(.7f)
				.aspectRatio(3 / 4f)
		) {

			Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
				SpacerH(height = 12.dp)
				Text(text = if (progress >= 1) "完成" else "正在分析中...", fontSize = 22.sp)
				SpacerH(height = 12.dp)
				Box(modifier = Modifier.fillMaxSize(.75f), contentAlignment = Alignment.Center) {
					if (doneFlag) {
						Text(text = "共发现${findSize}张缺少日期或者不匹配的照片", textAlign = TextAlign.Center)
					} else {
						Box(contentAlignment = Alignment.Center) {
							CircularProgressIndicator(
								progress = progress,
								modifier = Modifier
									.fillMaxWidth(.8f)
									.aspectRatio(1f, true),
								strokeWidth = 8.dp
							)

							Text(text = "$currentIndex/$total", fontSize = 18.sp)
						}
					}
				}
				SpacerH(height = 12.dp)
				if (progress >= 1) {
					ElevatedButton(onClick = { confrimClick.invoke() }) {
						Text(text = "查看")
					}
				} else {
					Text(
						text = currentFileName,
						fontSize = 16.sp,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1,
						modifier = Modifier.padding(horizontal = 12.dp)
					)
				}
			}
		}
	}
}

@Composable
fun ImageItem(info: ImageInfoEntity, click: () -> Unit) {
	ElevatedCard(
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(1f)
			.clickable(onClick = click)
	) {
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current).data(info.path).crossfade(true)
				.build(),
			contentDescription = "img",
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop,
			filterQuality = FilterQuality.None
		)
	}
}
