package com.example.the_2048.presentation.gamefield.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.a2048.R
import com.example.the_2048.presentation.gamefield.presenter.GamePresenter
import kotlinx.android.synthetic.main.fragment_field.*

class GameFragment : MvpAppCompatFragment(), IGameFragment, View.OnTouchListener {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    companion object {
        fun newInstance() = GameFragment()
    }

    var x = 0F
    var y = 0F
    var firstX = 0F
    var firstY = 0F

    lateinit var viewList: List<TextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewList = listOf(
            cell_0, cell_1, cell_2, cell_3, cell_4, cell_5,
            cell_6, cell_7, cell_8, cell_9, cell_10, cell_11, cell_12,
            cell_13, cell_14, cell_15
        )
        gamePresenter.startNewGame()
        game_field.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        x = event.x
        y = event.y

        when (event.action) {
            ACTION_DOWN -> {
                firstX = event.x
                firstY = event.y
            }
            ACTION_UP -> {
                getDirection(firstX, firstY, x, y)
            }
        }
        return true
    }

    private fun getDirection(firstX: Float, firstY: Float, lastX: Float, lastY: Float) {
        val yLength = Math.abs(lastY - firstY)
        val xLength = Math.abs(lastX - firstX)
        if (xLength >= yLength) {
            if (xLength < 30) return
            val difference = lastX - firstX
            if (difference >= 0) {
                gamePresenter.rightSwipe()
            } else {
                gamePresenter.leftSwipe()
            }
        } else {
            if (yLength < 30) return
            val difference = lastY - firstY
            if (difference >= 0) {
                gamePresenter.downSwipe()
            } else {
                gamePresenter.upSwipe()
            }
        }
    }

    override fun drawField(listOfCellsValues: List<String>) {
        viewList.forEachIndexed { index, textView -> viewTransformation(textView, listOfCellsValues[index]) }
    }

    override fun animateField(movedList: List<Int>) {
        movedList.forEach{position -> animate(viewList[position])}
    }

    private fun animate(view: View) {
        var set1 = AnimatorSet()
        set1.setDuration(150).playTogether(
            ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.1f),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.1f)
        )

        val set2 = AnimatorSet()
        set2.setDuration(150).playTogether(
            ObjectAnimator.ofFloat(view, View.SCALE_X, 1.1f, 1.0f),
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.1f, 1.0f)
        )
        set1.playSequentially(set2)
        set1.start()
    }

    private fun viewTransformation(view: TextView, strValue: String) {

        if (strValue.isBlank()) { //если клетка пустая, то отоброжаем ее пробелами и сохраняем ее положение в массивы
            if (view.text.isNotBlank()) {
                view.text = ""
                view.setBackgroundResource(R.drawable.rectangle)
            }
        } else if (strValue.toInt() < 10) {
            when (strValue) {
                "2" -> {
                    if (view.text != "2") {
                        view.text = "2"
                        view.setBackgroundResource(R.drawable.rectangle2)
                        view.textSize = 36.toFloat()
                        view.setTextColor(Color.parseColor("#766D64"))
                    }
                }
                "4" -> {
                    if (view.text != "4") {
                        view.text = "4"
                        view.setBackgroundResource(R.drawable.rectangle4)
                        view.textSize = 36.toFloat()
                        view.setTextColor(Color.parseColor("#766D64"))
                    }
                }
                else -> {
                    if (view.text != "8") {
                        view.text = "8"
                        view.setBackgroundResource(R.drawable.rectangle8)
                        view.textSize = 36.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
            }
        } else if (strValue.toInt() in 16..127) {
            when (strValue) {
                "16" -> {
                    if (view.text != "16") {
                        view.text = "16"
                        view.setBackgroundResource(R.drawable.rectangle16)
                        view.textSize = 38.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
                "32" -> {
                    if (view.text != "32") {
                        view.text = "32"
                        view.setBackgroundResource(R.drawable.rectangle32)
                        view.textSize = 38.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
                "64" -> {
                    if (view.text != "64") {
                        view.text = "64"
                        view.setBackgroundResource(R.drawable.rectangle64)
                        view.textSize = 38.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
            }
        } else if (strValue.toInt() in 128..1023) {
            when (strValue) {
                "128" -> {
                    if (view.text != "128") {
                        view.text = "128"
                        view.setBackgroundResource(R.drawable.rectangle128)
                        view.textSize = 32.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
                "256" -> {
                    if (view.text != "256") {
                        view.text = "256"
                        view.setBackgroundResource(R.drawable.rectangle256)
                        view.textSize = 32.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
                "512" -> {
                    if (view.text != "512") {
                        view.text = "512"
                        view.setBackgroundResource(R.drawable.rectangle512)
                        view.textSize = 32.toFloat()
                        view.setTextColor(Color.parseColor("#FFFCF3"))
                    }
                }
            }
        } else {
            if (view.text != strValue) {
                view.text = strValue
                view.setBackgroundResource(R.drawable.rectangle1024)
                view.textSize = 25.toFloat()
                view.setTextColor(Color.parseColor("#FFFCF3"))
            }
        }
    }

}




