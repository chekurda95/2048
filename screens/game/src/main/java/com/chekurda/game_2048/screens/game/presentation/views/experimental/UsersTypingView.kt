package com.chekurda.game_2048.screens.game.presentation.views.experimental

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.chekurda.design.custom_view_tools.TextLayout
import com.chekurda.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import com.chekurda.design.custom_view_tools.utils.safeRequestLayout
import com.chekurda.design.custom_view_tools.utils.sp
import com.chekurda.game_2048.screens.game.R
import org.apache.commons.lang3.StringUtils

class UsersTypingView(context: Context) : ViewGroup(context) {

    class UsersPrintingData(
        val fewUsers: List<MockUserName> = emptyList(),
        val count: Int = 0
    )

    var data: UsersPrintingData = UsersPrintingData()
        set(value) {
            val isChanged = data != value
            field = value
            if (isChanged) {
                textLayout.buildLayout { text = makeUsersPrintingText() }
                safeRequestLayout()
            }
        }

    @Px var textSize: Float = sp(DEFAULT_TEXT_SIZE_SP).toFloat()
        set(value) {
            val isChanged = textLayout.configure { textSize = value }
            field = value
            if (isChanged) safeRequestLayout()
        }

    @ColorInt var textColor: Int = Color.GRAY
        set(value) {
            field = value
            textLayout.textPaint.color = value
        }

    private val onePrintingText = resources.getString(R.string.one_typing)
    private val fewPrintingText = resources.getString(R.string.few_typing)

    private val textLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
    }

    private val activeDotsView = ActiveDotsView(context).apply {
        params = ActiveDotsView.Params(dotSize = (textSize * ACTIVE_POINTS_SIZE_PERCENT).toInt())
    }

    init {
        setWillNotDraw(false)
        addView(activeDotsView)
    }

    private fun makeUsersPrintingText(): String =
        if (data.count < 3) {
            val userNameList = if (data.fewUsers.size < 3) {
                data.fewUsers.map { it.renderName }.filter { it.isNotBlank() }
            } else emptyList()

            when (userNameList.size) {
                1 -> "${userNameList.first()} $onePrintingText"
                2 -> userNameList.joinToString(postfix = StringUtils.SPACE + fewPrintingText)
                else -> StringUtils.EMPTY
            }
        } else {
            resources.getQuantityString(R.plurals.participants_typing, data.count, data.count)
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChild(activeDotsView, makeUnspecifiedSpec(), makeUnspecifiedSpec())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        textLayout.layout(0, 0)
        val dotsTop = textLayout.top + textLayout.baseline - activeDotsView.baseline
        activeDotsView.layout(
            textLayout.right,
            textLayout.top + textLayout.baseline - activeDotsView.baseline,
            textLayout.right + activeDotsView.measuredWidth,
            dotsTop + activeDotsView.measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        textLayout.draw(canvas)
    }

    override fun hasOverlappingRendering(): Boolean = false
}

private const val ACTIVE_POINTS_SIZE_PERCENT = 0.15
private const val DEFAULT_TEXT_SIZE_SP = 14

data class MockUserName(val lastName: String, val firstName: String) {
    val renderName: String
        get() {
            val isEmptyLast = lastName.isBlank()
            val isEmptyFirst = firstName.isBlank()
            return when {
                !isEmptyFirst && !isEmptyLast -> "$lastName ${firstName.first()}."
                isEmptyFirst && isEmptyLast -> StringUtils.EMPTY
                isEmptyFirst -> lastName
                isEmptyLast -> firstName
                else -> StringUtils.EMPTY
            }
        }
}