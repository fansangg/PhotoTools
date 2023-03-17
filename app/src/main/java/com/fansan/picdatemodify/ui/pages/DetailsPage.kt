package com.fansan.picdatemodify.ui.pages

import android.app.Activity
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore.createWriteRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.fansan.picdatemodify.R
import com.fansan.picdatemodify.common.CommonButton
import com.fansan.picdatemodify.entity.ImageInfoEntity
import com.fansan.picdatemodify.ui.widgets.SpacerH
import com.fansan.picdatemodify.ui.widgets.SpacerW
import com.fansan.picdatemodify.ui.widgets.TitleColumn
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

	val lastModifyTime = remember {
		mutableStateOf(info.lastModified * 1000)
	}

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartIntentSenderForResult(),
		onResult = {
			if (it.resultCode == Activity.RESULT_OK) {
				asyncDate(file, info, lastModifyTime, context, navHostController)
			} else {
				ToastUtils.showShort("请允许修改来同步日期")
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
			Icon(painter = painterResource(id = R.mipmap.image), contentDescription = "img")
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
			Icon(painter = painterResource(id = R.mipmap.edie_calendar), contentDescription = "img")
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
				Icon(painter = painterResource(id = R.mipmap.edie_calendar), contentDescription = "img")
				SpacerW(width = 12.dp)
				Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
					Text(
						text = TimeUtils.millis2String(lastModifyTime.value),
						fontWeight = FontWeight.SemiBold,
						fontSize = 16.sp
					)
					Text(
						text = "修改日期", fontWeight = FontWeight.SemiBold, fontSize = 16.sp
					)
				}
			}

			if (lastModifyTime.value != info.taken) {
				SpacerH(height = 24.dp)
				CommonButton(content = "同步日期",modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
					if (file.canWrite()) {
						asyncDate(file, info, lastModifyTime, context, navHostController)
					} else {
						val editPendingIntent = createWriteRequest(
							context.contentResolver, listOf(Uri.parse(info.uri))
						)
						launcher.launch(IntentSenderRequest.Builder(editPendingIntent).build())
					}
				}
			}
		}

	}
}

private fun asyncDate(
	file: File,
	info: ImageInfoEntity,
	lastModifyTime: MutableState<Long>,
	context: Context,
	navHostController: NavHostController
) {
	val result = fixFunc(file, info.taken)
	if (result) lastModifyTime.value = info.taken
	MediaScannerConnection.scanFile(
		context, arrayOf(info.path), null
	) { _, _ ->

	}
	navHostController.previousBackStackEntry?.savedStateHandle?.set("path", info.path)
}

private fun fixFunc(file: File, time: Long): Boolean {
	return file.setLastModified(time)
}