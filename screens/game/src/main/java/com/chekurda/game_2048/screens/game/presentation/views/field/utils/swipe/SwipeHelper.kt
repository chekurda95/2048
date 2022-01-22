package com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.chekurda.design.custom_view_tools.utils.dp
import com.chekurda.game_2048.screens.game.presentation.views.field.utils.swipe.SwipeDirection.*
import kotlin.math.abs

internal class SwipeHelper(
    context: Context,
    private var listener: SwipeListener
) : OnTouchListener {

    private val swipeDistance = context.dp(SWIPE_DISTANCE_DP)
    private var isSwipeRunning = false

    private val gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean =
            true.also { isSwipeRunning = true }

        override fun onScroll(
            eventDown: MotionEvent,
            eventMove: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val dx = eventMove.x - eventDown.x
            val dy = eventMove.y - eventDown.y
            val xDistance = abs(dx)
            val yDistance = abs(dy)

            return (isSwipeRunning && (xDistance >= swipeDistance || yDistance >= swipeDistance)).also { isSwipe ->
                if (isSwipe) {
                    val direction = if (xDistance >= yDistance) {
                        if (dx > 0) RIGHT else LEFT
                    } else {
                        if (dy > 0) DOWN else UP
                    }

                    listener.onSwipe(direction)
                    isSwipeRunning = false
                }
            }
        }
    })

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean =
        gestureDetector.onTouchEvent(event)
}

private const val SWIPE_DISTANCE_DP = 20