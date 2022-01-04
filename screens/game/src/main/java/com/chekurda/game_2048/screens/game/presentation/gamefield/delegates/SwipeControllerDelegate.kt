package com.chekurda.game_2048.screens.game.presentation.gamefield.delegates

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.chekurda.game_2048.screens.game.presentation.gamefield.delegates.SwipeDirection.*
import kotlin.math.abs

interface SwipeDelegate : View.OnTouchListener {

    fun initSwipeDelegate(view: View, listener: SwipeListener)
}

class SwipeControllerDelegate : SwipeDelegate {

    private lateinit var swipeListener: SwipeListener
    private val actionDownPosition = TouchPosition()

    override fun initSwipeDelegate(view: View, listener: SwipeListener) {
        view.setOnTouchListener(this)
        swipeListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDownPosition.x = event.x
                actionDownPosition.y = event.y
            }
            MotionEvent.ACTION_UP   -> {
                getDirection(actionDownPosition.x, actionDownPosition.y, event.x, event.y)
                    ?.let(swipeListener::onSwipe)
            }
        }
        return true
    }

    private fun getDirection(firstX: Float, firstY: Float, lastX: Float, lastY: Float): SwipeDirection? {
        val differenceX = lastX - firstX
        val differenceY = lastY - firstY
        val xLength = abs(differenceX)
        val yLength = abs(differenceY)

        if (xLength < minSwipeLength && yLength < minSwipeLength) return null

        return if (xLength >= yLength) {
            if (differenceX > 0) RIGHT else LEFT
        } else {
            if (differenceY > 0) DOWN else UP
        }
    }
}

private data class TouchPosition(var x: Float = 0f, var y: Float = 0f)

private const val minSwipeLength = 30