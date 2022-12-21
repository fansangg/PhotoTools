package com.fansan.exiffix.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 *@author  fansan
 *@version 2022/12/20
 */

@Composable
fun TitleColumn(
	title: String,
	backClick: () -> Unit,
	modifier: Modifier = Modifier,
	withBackIcon: Boolean = true,
	content: @Composable ColumnScope.() -> Unit
) {
	Column(modifier = Modifier.fillMaxSize()
		.background(color = MaterialTheme.colorScheme.background).then(modifier)) {
		CommonTitle(title = title, withBackIcon = withBackIcon, backClick = backClick)
		content()
	}
}