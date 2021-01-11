package com.example.the_2048.presentation.gamefield.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.a2048.R
import com.example.the_2048.presentation.gamefield.presenter.GamePresenter
import com.example.the_2048.presentation.views.cell.CellView
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

    lateinit var viewList: List<CellView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewList = listOf(
            cell_0, cell_1, cell_2, cell_3,
            cell_4, cell_5, cell_6, cell_7,
            cell_8, cell_9, cell_10, cell_11,
            cell_12, cell_13, cell_14, cell_15
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

    override fun drawField(listOfCellsValues: List<Int>) {
        viewList.forEachIndexed { index, cellView -> cellView.setValue(listOfCellsValues[index]) }
    }

    override fun animateField(movedList: List<Int>) {
        movedList.forEach { position -> viewList[position].animateGrowing() }
    }
}




