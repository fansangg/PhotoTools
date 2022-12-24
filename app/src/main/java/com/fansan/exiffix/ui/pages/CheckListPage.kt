package com.fansan.exiffix.ui.pages

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.TimeUtils
import com.fansan.exiffix.ui.entity.ErrorFile
import com.fansan.exiffix.ui.entity.ErrorType
import com.fansan.exiffix.ui.widgets.SpacerW
import com.fansan.exiffix.ui.widgets.TitleColumn
import com.google.gson.Gson
import java.io.File

/**
 *@author  fansan
 *@version 2022/12/22
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckListPage(navHostController: NavHostController, list: List<ErrorFile>) {

	val typeList = list.groupBy {
		it.type
	}

	TitleColumn(title = "Result List", backClick = { navHostController.popBackStack() }) {
		LazyColumn(
			modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 4.dp)
		) {


			stickyHeader {
				GroupHeader(errorInfo = "No Date")
			}

			if (typeList[ErrorType.NOEXIF].isNullOrEmpty()) {
				item {
					EmptyItem()
				}
			} else {
				items(typeList[ErrorType.NOEXIF]!!) {
					ErrorDetailsCard(error = it){
						navHostController.navigate("DETAILSPAGE/${Uri.encode(GsonUtils.toJson(it))}")
					}
				}
			}

			stickyHeader {
				GroupHeader(errorInfo = "Exif Date Not Match")
			}

			if (typeList[ErrorType.DATENOMATCH].isNullOrEmpty()) {
				item {
					EmptyItem()
				}
			} else {
				items(typeList[ErrorType.DATENOMATCH]!!) {
					ErrorDetailsCard(error = it){
						navHostController.navigate("DETAILSPAGE/${Uri.encode(GsonUtils.toJson(it))}")
					}
				}
			}

			stickyHeader {
				GroupHeader(errorInfo = "Other Error")
			}

			if (typeList[ErrorType.OTHERERROR].isNullOrEmpty()) {
				item {
					EmptyItem()
				}
			} else {
				items(typeList[ErrorType.OTHERERROR]!!) {
					ErrorDetailsCard(error = it){
						navHostController.navigate("DETAILSPAGE/${Uri.encode(GsonUtils.toJson(it))}")
					}
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
fun ErrorDetailsCard(error: ErrorFile, click: () -> Unit) {
	val file = remember {
		File(error.path)
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.wrapContentHeight()
			.padding(vertical = 6.dp, horizontal = 8.dp)
			.clickable(onClick = click),
		verticalAlignment = Alignment.CenterVertically
	) {

		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current).data(error.path).crossfade(true)
				.build(),
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

@Composable
fun EmptyItem() {
	Text(
		text = "Nothing",
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 12.dp),
		textAlign = TextAlign.Center
	)
}