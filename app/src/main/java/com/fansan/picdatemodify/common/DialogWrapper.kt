package com.fansan.picdatemodify.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogWrapper(dismiss:(() -> Unit)? = null,dismissOnClickOutside:Boolean = false, dismissOnBackPress:Boolean = false, usePlatformDefaultWidth:Boolean = false,content:@Composable () -> Unit){
	Dialog(onDismissRequest = { dismiss?.invoke() }, properties = DialogProperties(dismissOnClickOutside = dismissOnClickOutside, dismissOnBackPress = dismissOnBackPress, usePlatformDefaultWidth = usePlatformDefaultWidth)) {
		content()
	}
}