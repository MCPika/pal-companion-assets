package com.example.palcompanion

import android.app.Application
import com.example.palcompanion.data.AppContainer
import com.example.palcompanion.data.DefaultAppContainer

class PalCompanionApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
