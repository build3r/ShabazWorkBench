package com.builder.shabazworkbench

import android.app.Application
import androidx.datastore.core.DataStore
import java.util.prefs.Preferences

class App: Application() {
    init {
        val dataStore: DataStore<Preferences> = this.applicationContext.createDataStore(
            name = "settings"
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}