package com.fansan.picdatemodify.ui.state

import androidx.compose.runtime.*

class ModifyFileNameState {

	var selectedAlbumName = "all"
	var prefix = "_"
	var format = "yyyyMMddHHmmss"
	var symbol = "_"
	var useTaken = true
	var skipNoTaken = false
	val showDialogState: State<Boolean> get() = _showDialogState
	private val _showDialogState = mutableStateOf(false)

	val showWarningState: State<Boolean> get() = _showWarningState
	private val _showWarningState = mutableStateOf(false)

	val modifiedFileNameSuccessCount:State<Int> get() = _modifiedFileNameSuccessCount
	private val _modifiedFileNameSuccessCount = mutableStateOf(0)
	val modifiedFileNameSkipCount:State<Int> get() = _modifiedFileNameSkipCount
	private val _modifiedFileNameSkipCount = mutableStateOf(0)
	val modifiedFileNameFieldCount :State<Int> get() = _modifiedFileNameFieldCount
	private val _modifiedFileNameFieldCount = mutableStateOf(0)
	val modifiedFileNameCurrentIndex : State<Int> get() = _modifiedFileNameCurrentIndex
	private val _modifiedFileNameCurrentIndex = mutableStateOf(0)
	val modifiedFileNameListCount : State<Int> get() = _modifiedFileNameListCount
	private val _modifiedFileNameListCount = mutableStateOf(0)
	val modifiedFileNameTaskState:State<ModifiedFileTaskState> get()  = _modifiedFileNameTaskState
	private val _modifiedFileNameTaskState:MutableState<ModifiedFileTaskState> = mutableStateOf(ModifiedFileTaskState.Idel)

	fun showDialog(){
		_showDialogState.value = true
	}

	fun dismissDialog(){
		_showDialogState.value = false
	}

	fun showWarning(){
		_showWarningState.value = true
	}

	fun dismissWarning(){
		_showWarningState.value = false
	}

	fun increaseSuccess(){
		_modifiedFileNameSuccessCount.value = _modifiedFileNameSuccessCount.value + 1
	}

	fun increaseFailed(){
		_modifiedFileNameFieldCount.value = _modifiedFileNameFieldCount.value + 1
	}

	fun increaseCurrentIndex(){
		_modifiedFileNameCurrentIndex.value = _modifiedFileNameCurrentIndex.value + 1
	}

	fun increaseSkipCount(){
		_modifiedFileNameSkipCount.value = _modifiedFileNameSkipCount.value + 1
	}

	fun setListCount(count:Int){
		_modifiedFileNameListCount.value = count
	}

	fun taskInProgress(){
		_modifiedFileNameTaskState.value = ModifiedFileTaskState.InProgress
	}

	fun taskDone(){
		_modifiedFileNameTaskState.value = ModifiedFileTaskState.Done
	}

	fun resetAll(){
		_modifiedFileNameSuccessCount.value = 0
		_modifiedFileNameFieldCount.value = 0
		_modifiedFileNameCurrentIndex.value = 0
		_modifiedFileNameListCount.value = 0
		_modifiedFileNameSkipCount.value = 0
		_modifiedFileNameTaskState.value = ModifiedFileTaskState.Idel
		prefix = "_"
		format = "yyyyMMddHHmmss"
		symbol = "_"
		useTaken = true
		skipNoTaken = false
	}
}

sealed class ModifiedFileTaskState{
	object Idel:ModifiedFileTaskState()
	object InProgress:ModifiedFileTaskState()
	object Done:ModifiedFileTaskState()
}