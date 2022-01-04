package com.chekurda.game_2048.screens.game.presentation.views.cell

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import org.apache.commons.lang3.StringUtils.EMPTY

class CellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun setValue(value: Int) {
        value.params.let {
            if (it.value != text) {
                text = it.value
                textSize = it.textSize
                setTextColor(it.textColor)
                setBackgroundResource(it.backgroundRes)
            }
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val textValue = if (text == "0") EMPTY else text
        super.setText(textValue, type)
    }

    fun animateGrowing() {
        val growDownAnimator = AnimatorSet().apply {
            setDuration(GROW_DURATION_MLS).playTogether(
                ObjectAnimator.ofFloat(this@CellView, SCALE_X, MAX_SCALE, NORMAL_SCALE),
                ObjectAnimator.ofFloat(this@CellView, SCALE_Y, MAX_SCALE, NORMAL_SCALE)
            )
        }

        val growUpAnimator = AnimatorSet().apply {
            setDuration(GROW_DURATION_MLS).playTogether(
                ObjectAnimator.ofFloat(this@CellView, SCALE_X, NORMAL_SCALE, MAX_SCALE),
                ObjectAnimator.ofFloat(this@CellView, SCALE_Y, NORMAL_SCALE, MAX_SCALE)
            )
            playSequentially(growDownAnimator)
        }

        growUpAnimator.start()
    }

    private val Int.params: CellParams
        get() = CellParams.values().firstOrNull { it.value == toString() } ?: CellParams.C_0
}

private const val GROW_DURATION_MLS = 150L
private const val NORMAL_SCALE = 1.0f
private const val MAX_SCALE = 1.1f
