package com.chekurda.game_2048.screens.game.contract

import androidx.fragment.app.Fragment
import com.chekurda.common.plugin_struct.Feature

interface GameFragmentFactory : Feature {

    fun createGameFragment(): Fragment
}