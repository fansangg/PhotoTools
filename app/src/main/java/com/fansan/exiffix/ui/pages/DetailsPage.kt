package com.fansan.exiffix.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.ui.common.logd
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.widgets.SpacerH
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/24
 */

@Composable
fun DetailsPage(navHostController: NavHostController, errorFile: ErrorFile) {
	val file = remember {
		File(errorFile.path)
	}
	var lastModifyTime by remember {
		mutableStateOf(file.lastModified())
	}
	val maxWidth = LocalConfiguration.current.screenWidthDp.dp - 24.dp
	TitleColumn(title = "Details", backClick = { navHostController.popBackStack() }) {

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
				Text(text = file.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
				Text(text = FileUtils.getDirName(file), fontSize = 12.sp, color = Color(0xff888888))
				Row {
					Text(
						text = ConvertUtils.byte2FitMemorySize(file.length(), 0),
						fontSize = 12.sp,
						color = Color(0xff888888)
					)
					SpacerW(width = 12.dp)
					Text(
						text = "${ImageUtils.getSize(file)[0]} x ${ImageUtils.getSize(file)[1]}",
						fontSize = 12.sp,
						color = Color(0xff888888)
					)
				}
			}
		}

		SpacerH(height = 24.dp)

		AsyncImage(
			model = errorFile.path,
			contentDescription = "img",
			modifier = Modifier
				.widthIn(max = maxWidth)
				.heightIn(max = 350.dp)
				.align(alignment = Alignment.CenterHorizontally)
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
					text = errorFile.exifDate ?: "No Exif Date",
					fontWeight = FontWeight.SemiBold,
					fontSize = 16.sp
				)
				Text(text = "Exif Date Info", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
			}
		}

		if (errorFile.exifDate != null) {
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
						text = "File last modify time",
						fontWeight = FontWeight.SemiBold,
						fontSize = 16.sp
					)
				}
			}

			if (lastModifyTime != TimeUtils.string2Millis(errorFile.exifDate)) {
				SpacerH(height = 24.dp)
				ElevatedButton(onClick = {
					val mills = TimeUtils.string2Millis(errorFile.exifDate)
					val flag = file.setLastModified(mills)
					//todo android 13 权限问题
					"flag == $flag".logd()
					lastModifyTime = mills
				}, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
					Text(text = "Fix")
				}
			}
		}

	}
}