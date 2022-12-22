package com.fansan.exiffix.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.entity.ErrorType
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/22
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckListPage(navHostController: NavHostController, list: List<ErrorFile>) {

	val noExifList = list.filter {
		it.type == ErrorType.NOEXIF
	}
	val dateNoMathList = list.filter {
		it.type == ErrorType.DATENOMATCH
	}
	val otherErrorList = list.filter {
		it.type == ErrorType.OTHERERROR
	}

	TitleColumn(title = "CheckList", backClick = { navHostController.popBackStack() }) {
		LazyColumn(
			modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 4.dp)
		) {

			if (noExifList.isNotEmpty()) {
				stickyHeader {
					GroupHeader(errorInfo = "NO EXIF DATE")
				}

				items(noExifList) {
					ErrorDetailsCard(error = it)
				}
			}

			if (dateNoMathList.isNotEmpty()) {
				stickyHeader {
					GroupHeader(errorInfo = "DATE NOT MATCH")
				}

				items(dateNoMathList) {
					ErrorDetailsCard(error = it)
				}
			}

			if (otherErrorList.isNotEmpty()) {
				stickyHeader {
					GroupHeader(errorInfo = "OTHER ERROR")
				}

				items(otherErrorList) {
					ErrorDetailsCard(error = it)
				}
			}
		}
	}
}

@Composable
fun GroupHeader(errorInfo: String) {

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(30.dp)
			.background(color = MaterialTheme.colorScheme.primaryContainer)
			.padding(start = 12.dp),
		contentAlignment = Alignment.CenterStart
	) {

		Text(text = errorInfo, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
	}
}

@Composable
fun ErrorDetailsCard(error: ErrorFile) {
	val file = remember {
		File(error.path)
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(vertical = 6.dp, horizontal = 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {

		AsyncImage(
			model = error.path,
			contentDescription = "thumbnail",
			contentScale = ContentScale.Crop,
			filterQuality = FilterQuality.Low,
			modifier = Modifier.size(40.dp)
		)

		SpacerW(width = 10.dp)

		Column(verticalArrangement = Arrangement.SpaceAround) {
			Text(text = file.name)
			Text(text = TimeUtils.millis2String(file.lastModified()), fontSize = 10.sp)
		}
	}
}