package com.sfdex.transgenic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ctx = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var ctx: Context? = null
        fun getContext(): Context = ctx!!
    }
}