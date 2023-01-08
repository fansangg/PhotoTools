package com.fansan.exiffix.ui.pages

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.provider.MediaStore.createWriteRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.fansan.exiffix.common.CommonButton
import com.fansan.exiffix.common.logd
import com.fansan.exiffix.entity.ErrorFile
import com.fansan.exiffix.entity.ImageInfoEntity
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/24
 */

@Composable
fun DetailsPage(navHostController: NavHostController, info: ImageInfoEntity) {
	val file = remember {
		File(info.path)
	}

	val context = LocalContext.current

	var lastModifyTime by remember {
		mutableStateOf(info.lastModified * 1000)
	}
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartIntentSenderForResult(),
		onResult = {
			if (it.resultCode == Activity.RESULT_OK) {
				val result = fixFunc(file, info.taken)
				if (result) lastModifyTime = info.taken
				MediaScannerConnection.scanFile(
					context, arrayOf(info.path), null
				) { _, _ ->

				}
				navHostController.previousBackStackEntry?.savedStateHandle?.set("path",info.path)
			} else {
				ToastUtils.showShort("请允许修改此照片")
			}
		})

	val maxWidth = LocalConfiguration.current.screenWidthDp.dp - 24.dp
	TitleColumn(title = "详情", backClick = { navHostController.popBackStack() }) {

		SpacerH(height = 20.dp)

		Row(
			Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(imageVector = Icons.Default.Image, contentDescription = "img")
			SpacerW(width = 12.dp)
			Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
				Text(text = info.displayName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
				Text(
					text = info.path.substring(0, info.path.lastIndexOf("/")),
					fontSize = 12.sp,
					color = Color(0xff888888)
				)
				Row {
					Text(
						text = ConvertUtils.byte2FitMemorySize(info.size, 0),
						fontSize = 12.sp,
						color = Color(0xff888888)
					)
					SpacerW(width = 12.dp)
					Text(
						text = "${info.width}x${info.height}",
						fontSize = 12.sp,
						color = Color(0xff888888)
					)
				}
			}
		}

		SpacerH(height = 24.dp)

		AsyncImage(
			model = info.path,
			contentDescription = "img",
			modifier = Modifier
				.widthIn(max = maxWidth)
				.heightIn(max = 350.dp)
				.align(alignment = Alignment.CenterHorizontally),
			filterQuality = FilterQuality.Medium
		)

		SpacerH(height = 24.dp)

		Row(
			Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "img")
			SpacerW(width = 12.dp)
			Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
				Text(
					text = if (info.taken > 0) TimeUtils.millis2String(info.taken) else "没有日期信息",
					fontWeight = FontWeight.SemiBold,
					fontSize = 16.sp
				)
				Text(text = "元数据的日期信息", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
			}
		}

		if (info.taken > 0) {
			SpacerH(height = 24.dp)
			Row(
				Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "img")
				SpacerW(width = 12.dp)
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = TimeUtils.millis2String(lastModifyTime),
						fontWeight = FontWeight.SemiBold,
						fontSize = 16.sp
					)
					Text(
						text = "最后修改日期", fontWeight = FontWeight.SemiBold, fontSize = 16.sp
					)
				}
			}

			if (lastModifyTime != info.taken) {
				SpacerH(height = 24.dp)
				CommonButton(content = "修复",modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
					if (file.canWrite()) {
						val result = fixFunc(file, info.taken)
						if (result) lastModifyTime = info.taken
						MediaScannerConnection.scanFile(
							context, arrayOf(info.path), null
						) { _, _ ->

						}
						navHostController.previousBackStackEntry?.savedStateHandle?.set("path",info.path)
					} else {
						val editPendingIntent =
							createWriteRequest(context.contentResolver, listOf(Uri.parse(info.uri)))
						launcher.launch(IntentSenderRequest.Builder(editPendingIntent).build())
					}
				}
			}
		}

	}
}

private fun fixFunc(file: File, time: Long): Boolean {
	return file.setLastModified(time)
}