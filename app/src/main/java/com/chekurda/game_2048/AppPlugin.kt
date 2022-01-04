package com.chekurda.game_2048

import com.chekurda.common.plugin_struct.*
import com.chekurda.game_2048.screens.game.contract.GameFragmentFactory

object AppPlugin : BasePlugin<Unit>() {

    private lateinit var gameFragmentFactoryFeature: FeatureProvider<GameFragmentFactory>

    val gameFragmentFactory: GameFragmentFactory by lazy {
        gameFragmentFactoryFeature.get()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf()
    override val customizationOptions: Unit = Unit

    override val dependency: Dependency = Dependency.Builder()
        .require(GameFragmentFactory::class.java) { gameFragmentFactoryFeature = it }
        .build()
}