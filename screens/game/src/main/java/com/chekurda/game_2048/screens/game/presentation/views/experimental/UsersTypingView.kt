package com.chekurda.game_2048.screens.game.presentation.views.experimental

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.chekurda.design.custom_view_tools.TextLayout
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import com.chekurda.design.custom_view_tools.utils.safeRequestLayout
import com.chekurda.design.custom_view_tools.utils.sp
import com.chekurda.design.custom_view_tools.utils.textHeight
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.presentation.views.experimental.TypingDotsView.Params
import org.apache.commons.lang3.StringUtils.EMPTY
import org.apache.commons.lang3.StringUtils.SPACE

class UsersTypingView(context: Context) : ViewGroup(context) {

    class UsersTypingData(
        val typingUsers: List<MockUserName> = emptyList(),
        val showUsers: Boolean = true
    )

    var data: UsersTypingData = UsersTypingData()
        set(value) {
            val isChanged = data != value
            field = value
            if (isChanged) onDataSetChanged()
        }

    @Px var textSize: Float = sp(DEFAULT_TEXT_SIZE_SP).toFloat()
        set(value) {
            val usersChanged = usersLayout.configure { textSize = value }
            val typingChanged = typingLayout.configure { textSize = value }
            typingDotsView.params = Params(dotSize = (value * ACTIVE_POINTS_SIZE_PERCENT).toInt())

            field = value
            if (usersChanged || typingChanged) safeRequestLayout()
        }

    @ColorInt var textColor: Int = Color.GRAY
        set(value) {
            field = value
            usersLayout.textPaint.color = value
        }

    private val oneTypingText = resources.getString(R.string.one_typing)
    private val fewPrintingText = resources.getString(R.string.few_typing)

    private val usersLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
    }

    private val typingLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
    }

    private val typingDotsView = TypingDotsView(context).apply {
        params = Params(dotSize = (textSize * ACTIVE_POINTS_SIZE_PERCENT).toInt())
    }

    init {
        setWillNotDraw(false)
        addView(typingDotsView)
    }

    private fun onDataSetChanged() {
        if (data.showUsers) {
            val userNameList = data.typingUsers
                .filter { it.lastOrFirst.isNotBlank() }
                .take(MAX_TYPING_USERS)

            if (userNameList.isNotEmpty()) {
                typingLayout.configure {
                    text = SPACE + if (userNameList.size > 1) fewPrintingText else oneTypingText
                }
                usersLayout.configure {
                    text = makeUsersTypingText(userNameList)
                    needHighWidthAccuracy = userNameList.size > 1
                    isVisible = true
                }

                safeRequestLayout()
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        } else {
            visibility = View.VISIBLE
            usersLayout.configure { isVisible = false }
            val isChanged = typingLayout.configure { text = oneTypingText }

            if (isChanged) safeRequestLayout()
        }
    }

    private fun makeUsersTypingText(users: List<MockUserName>): String =
        when (users.size) {
            0 -> EMPTY
            1 -> users.first().renderName
            else -> users.joinToString { it.lastOrFirst }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(typingDotsView, makeUnspecifiedSpec(), makeUnspecifiedSpec())
        setMeasuredDimension(
            measureDirection(widthMeasureSpec) { suggestedMinimumWidth },
            measureDirection(heightMeasureSpec) { suggestedMinimumHeight }
        )
    }

    @Px
    private fun measureDirection(measureSpec: Int, getMinSize: () -> Int): Int =
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(measureSpec)
            MeasureSpec.AT_MOST -> minOf(getMinSize(), MeasureSpec.getSize(measureSpec))
            else -> getMinSize()
        }

    override fun getSuggestedMinimumWidth(): Int {
        val minContentWidth = listOf(
            paddingStart,
            if (usersLayout.isVisible) usersLayout.getDesiredWidth(usersLayout.text) else 0,
            typingLayout.getDesiredWidth(typingLayout.text),
            typingDotsView.measuredWidth,
            paddingEnd
        ).sumBy { it }
        return maxOf(super.getSuggestedMinimumWidth(), minContentWidth)
    }

    override fun getSuggestedMinimumHeight(): Int =
        maxOf(super.getSuggestedMinimumHeight(), paddingTop + typingLayout.textPaint.textHeight + paddingBottom)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        usersLayout.configure {
            maxWidth = w - paddingStart - typingLayout.width - typingDotsView.measuredWidth - paddingEnd
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        usersLayout.layout(paddingStart, paddingTop)
        typingLayout.layout(usersLayout.right, usersLayout.top)
        val dotsTop = typingLayout.top + typingLayout.baseline - typingDotsView.baseline
        typingDotsView.layout(
            typingLayout.right,
            dotsTop,
            typingLayout.right + typingDotsView.measuredWidth,
            dotsTop + typingDotsView.measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        usersLayout.draw(canvas)
        typingLayout.draw(canvas)
    }

    override fun hasOverlappingRendering(): Boolean = false
}

private const val ACTIVE_POINTS_SIZE_PERCENT = 0.15
private const val DEFAULT_TEXT_SIZE_SP = 14
private const val MAX_TYPING_USERS = 10

data class MockUserName(val lastName: String, val firstName: String) {
    val renderName: String by lazy {
        val isEmptyLast = lastName.isBlank()
        val isEmptyFirst = firstName.isBlank()
        when {
            isEmptyFirst -> lastName
            isEmptyLast -> firstName
            else -> "$lastName ${firstName.first()}."
        }
    }

    val lastOrFirst: String
        get() = lastName.takeIf { it.isNotBlank() } ?: firstName
}