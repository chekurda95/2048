package com.chekurda.game_2048.screens.game

import com.chekurda.common.plugin_struct.BasePlugin
import com.chekurda.common.plugin_struct.Dependency
import com.chekurda.common.plugin_struct.Feature
import com.chekurda.common.plugin_struct.FeatureWrapper
import com.chekurda.game_2048.screens.game.contract.GameFragmentFactory
import com.chekurda.game_2048.screens.game.presentation.gamefield.view.GameFragment

object GamePlugin : BasePlugin<Unit>() {

    override val dependency: Dependency = Dependency.EMPTY
    override val customizationOptions: Unit = Unit

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(GameFragmentFactory::class.java) { GameFragment.Companion }
    )
}