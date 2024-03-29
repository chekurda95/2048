package com.chekurda.game_2048.presentation

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chekurda.game_2048.AppPlugin.gameFragmentFactory
import com.chekurda.game_2048.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fullscreen)
        ViewCompat.getWindowInsetsController(findViewById(R.id.fragment_container))!!
            .hide(WindowInsetsCompat.Type.statusBars())

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, gameFragmentFactory.createGameFragment())
                .commit()
        }
    }
}
