package com.example.android.writeitsayithearit

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.test.runner.AndroidJUnitRunner

class WriteItSayItHearItRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        MultiDex.install(targetContext)
        return super.newApplication(cl, AndroidTestApp::class.java.name, context)
    }
}