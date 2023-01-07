package com.fansan.exiffix.ui.state

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class ModifyFileNameState {

	val showDialogState: State<Boolean> get() = _showDialogState
	private val _showDialogState = mutableStateOf(false)

	fun showDialog(){
		_showDialogState.value = true
	}

	fun dismissDialog(){
		_showDialogState.value = false
	}
}