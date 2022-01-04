package com.chekurda.game_2048.app

import android.app.Application
import com.chekurda.common.plugin_manager.PluginManager
import com.chekurda.common.plugin_struct.Plugin
import com.chekurda.game_2048.AppPlugin
import com.chekurda.game_2048.screens.game.GamePlugin

/**
 * Система плагинов приложения.
 */
object PluginSystem {

    private val plugins: Array<Plugin<*>>
        get() = arrayOf(
            AppPlugin,
            GamePlugin
        )

    /**
     * Метод для инициализации плагинной системы.
     */
    fun initialize(
        app: Application,
        pluginManager: PluginManager = PluginManager()
    ) {
        pluginManager.registerPlugins(*plugins)
        pluginManager.configure(app)
    }
}