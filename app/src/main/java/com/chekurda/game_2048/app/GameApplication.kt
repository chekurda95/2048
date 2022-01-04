package com.chekurda.game_2048.app

import android.app.Application

/**
 * [Application] 2048.
 */
class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PluginSystem.initialize(this)
    }
}