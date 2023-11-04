package com.fansan.picdatemodify.ui.pages

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fansan.picdatemodify.entity.ImageInfoEntity

/**
 *@author  fansan
 *@version 2023/1/11
 */
 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExifDetailsPage(navHostController: NavHostController,info: ImageInfoEntity){

	LazyColumn(content = {
		items(count = 100){
			ListItem(headlineContent = {
				Text(text = "123")
			}, modifier = Modifier.height(50.dp))
		}
	})
}