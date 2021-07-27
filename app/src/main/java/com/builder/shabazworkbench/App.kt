package com.builder.shabazworkbench

import android.app.Application
import androidx.datastore.core.DataStore
import timber.log.Timber
import java.util.prefs.Preferences

class App: Application() {
    companion object {
        lateinit var instance: App
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
    }

}