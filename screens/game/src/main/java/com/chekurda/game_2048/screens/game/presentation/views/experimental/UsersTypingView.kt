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
import com.chekurda.game_2048.screens.game.presentation.views.experimental.TypingDotsView.DotsParams
import org.apache.commons.lang3.StringUtils.EMPTY
import org.apache.commons.lang3.StringUtils.SPACE

/**
 * View для отображения печатающих пользователей: "участник/и печатают...".
 * @see UsersTypingData
 *
 * @author vv.chekurda
 */
internal class UsersTypingView(context: Context) : ViewGroup(context) {

    /**
     * Данные о печатающих пользователях.
     *
     * @property typingUsers список имен печатающих пользователей.
     * @property typingCount общее количество всех печатающих пользователей.
     * @property isSingleUser true, если только один пользователь может печатать.
     */
    class UsersTypingData(
        val typingUsers: List<UserName> = emptyList(),
        val typingCount: Int = 0,
        val isSingleUser: Boolean = false
    )

    /**
     * Установить/получить данные о печатающих пользователях.
     */
    var data: UsersTypingData = UsersTypingData()
        set(value) {
            val isChanged = data != value
            field = value
            if (isChanged) onDataSetChanged()
        }

    /**
     * Установить/получить размер текста.
     */
    @Px
    var textSize: Float = sp(DEFAULT_TEXT_SIZE_SP).toFloat()
        set(value) {
            val usersChanged = usersLayout.configure { textSize = value }
            val typingChanged = typingLayout.configure { textSize = value }
            typingDotsView.params = DotsParams(size = (value * ACTIVE_POINTS_SIZE_PERCENT).toInt())

            field = value
            if (usersChanged || typingChanged) safeRequestLayout()
        }

    /**
     * Установить/получить цвет текста.
     */
    @ColorInt
    var textColor: Int = Color.GRAY
        set(value) {
            field = value
            usersLayout.textPaint.color = value
        }

    private val oneTypingText = resources.getString(R.string.one_typing)
    private val fewPrintingText = resources.getString(R.string.few_typing)

    /**
     * Разметка для отображения пользователей.
     */
    private val usersLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
    }

    /**
     * Разметка для отображения "печатает(ют)".
     */
    private val typingLayout = TextLayout {
        paint.textSize = textSize
        paint.color = textColor
    }

    /**
     * Анимируемые точки.
     */
    private val typingDotsView = TypingDotsView(context).apply {
        params = DotsParams(size = (textSize * ACTIVE_POINTS_SIZE_PERCENT).toInt())
    }

    init {
        setWillNotDraw(false)
        addView(typingDotsView)
    }

    /**
     * Произошло изменение данных для отображения печатающих пользователей.
     */
    private fun onDataSetChanged() {
        when {
            // Нет печатающих -> скрываем View
            data.typingCount <= 0 -> {
                visibility = View.GONE
            }

            // Единственный участник диалога -> показываем только "печатает"
            data.isSingleUser -> {
                visibility = View.VISIBLE

                usersLayout.configure { isVisible = false }
                val isChanged = typingLayout.configure { text = oneTypingText }

                if (isChanged) safeRequestLayout()
            }

            // Количество печатающих не больше допустимого количества -> показываем фамилии через запятую и печатают
            data.typingCount <= MAX_TYPING_USERS_NAMES -> {
                val userNameList = data.typingUsers
                    .filter { it.lastOrFirst.isNotBlank() }
                    .take(MAX_TYPING_USERS_NAMES)

                if (userNameList.isNotEmpty()) {
                    visibility = View.VISIBLE

                    usersLayout.configure {
                        text = makeUsersTypingText(userNameList)
                        needHighWidthAccuracy = userNameList.size > 1
                        isVisible = true
                    }
                    typingLayout.configure {
                        text = SPACE + if (userNameList.size == 1) oneTypingText else fewPrintingText
                    }

                    safeRequestLayout()
                } else {
                    visibility = View.GONE
                }
            }

            // В остальных случаях показываем "N участников печатает"
            else -> {
                visibility = View.VISIBLE

                typingLayout.configure {
                    text = SPACE + resources.getQuantityString(
                        R.plurals.typing,
                        data.typingCount
                    )
                }
                usersLayout.configure {
                    text = resources.getQuantityString(
                        R.plurals.participants_count,
                        data.typingCount,
                        data.typingCount
                    )
                    needHighWidthAccuracy = false
                    isVisible = true
                }

                safeRequestLayout()
            }
        }
    }

    /**
     * Создать строковый литерал печатающих пользователей по списку [users].
     */
    private fun makeUsersTypingText(users: List<UserName>): String =
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
            else                -> getMinSize()
        }

    override fun getSuggestedMinimumWidth(): Int {
        val minContentWidth = listOf(
            paddingStart,
            if (usersLayout.isVisible) usersLayout.getDesiredWidth(usersLayout.text) else 0,
            typingLayout.getDesiredWidth(typingLayout.text),
            typingDotsView.measuredWidth,
            paddingEnd
        ).sumOf { it }
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

/**
 * Процент размера анимируемых точек относительно размера текста.
 */
private const val ACTIVE_POINTS_SIZE_PERCENT = 0.15

/**
 * Стандартный размер текста в sp.
 */
private const val DEFAULT_TEXT_SIZE_SP = 14

/**
 * Максимальное количество имён печатающих пользователей.
 */
private const val MAX_TYPING_USERS_NAMES = 2

internal data class UserName(val lastName: String, val firstName: String) {

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