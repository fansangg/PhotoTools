package com.fansan.exiffix

import android.app.Application
import android.content.Context
import me.weishu.reflection.Reflection

class MyApplication: Application() {

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		Reflection.unseal(base)
	}
}