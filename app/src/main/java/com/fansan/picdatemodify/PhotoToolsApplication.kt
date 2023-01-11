package com.fansan.picdatemodify

import android.app.Application
import android.content.Context
import me.weishu.reflection.Reflection

class PhotoToolsApplication: Application() {

	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		Reflection.unseal(base)
	}
}