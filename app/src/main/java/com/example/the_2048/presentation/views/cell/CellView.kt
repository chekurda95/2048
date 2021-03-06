package com.example.the_2048.presentation.views.cell

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.example.the_2048.utils.StringUtils

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
        val textValue = if (text == "0") StringUtils.EMPTY else text
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
