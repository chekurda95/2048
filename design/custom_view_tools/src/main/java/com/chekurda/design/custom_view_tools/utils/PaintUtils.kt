package com.chekurda.design.custom_view_tools.utils

import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.text.TextPaint
import androidx.annotation.Px
import kotlin.math.ceil

/**
 * [Paint] с настройкой [ANTI_ALIAS_FLAG]
 */
class AntiPaint : Paint(ANTI_ALIAS_FLAG)

/**
 * [TextPaint] с настройкой [ANTI_ALIAS_FLAG]
 */
class AntiTextPaint : TextPaint(ANTI_ALIAS_FLAG)

/**
 * Получить ширину текста для данного [TextPaint].
 */
@Px
fun TextPaint.getTextWidth(text: CharSequence): Int =
    measureText(text, 0, text.length).toInt()

/**
 * Получить высоту одной строчки текста для данного [TextPaint].
 */
@get:Px
val TextPaint.textHeight: Int
    get() = ceil(fontMetrics.descent - fontMetrics.ascent).toInt()

