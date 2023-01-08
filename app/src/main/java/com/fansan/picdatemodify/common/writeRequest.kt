package com.fansan.picdatemodify.common

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.blankj.utilcode.util.ToastUtils

@Composable
fun writeRequest(okFunction:(() -> Unit)? = null): ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> {
	return rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
	                                         onResult = {
		                                         if (it.resultCode == Activity.RESULT_OK) {
			                                         okFunction?.invoke()
		                                         } else {
			                                         ToastUtils.showShort("请允许修改这些照片")
		                                         }
	                                         })
}