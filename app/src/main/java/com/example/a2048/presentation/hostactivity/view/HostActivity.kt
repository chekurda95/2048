package com.example.a2048.presentation.hostactivity.view

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.arellomobile.mvp.MvpAppCompatActivity
import com.example.a2048.R
import com.example.a2048.presentation.gamefield.view.GameFragment

class HostActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_fullscreen)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        if(fragment == null){
            fragment = GameFragment.newInstance()
            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}
