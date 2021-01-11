package com.example.the_2048.presentation.gamefield.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.a2048.R
import com.example.the_2048.presentation.gamefield.delegates.SwipeControllerDelegate
import com.example.the_2048.presentation.gamefield.delegates.SwipeDelegate
import com.example.the_2048.presentation.gamefield.presenter.GamePresenter
import com.example.the_2048.presentation.views.cell.CellView
import kotlinx.android.synthetic.main.fragment_field.*

class GameFragment : MvpAppCompatFragment(),
    IGameFragment,
    SwipeDelegate by SwipeControllerDelegate() {

    companion object {

        fun newInstance() = GameFragment()
    }

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    private lateinit var viewList: List<CellView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_field, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewList = listOf(
            cell_0, cell_1, cell_2, cell_3,
            cell_4, cell_5, cell_6, cell_7,
            cell_8, cell_9, cell_10, cell_11,
            cell_12, cell_13, cell_14, cell_15
        )
        gamePresenter.startNewGame()
        initSwipeDelegate(game_field, gamePresenter)
    }

    override fun drawField(listOfCellsValues: List<Int>) {
        viewList.forEachIndexed { index, cellView -> cellView.setValue(listOfCellsValues[index]) }
    }

    override fun animateField(movedList: List<Int>) {
        movedList.forEach { position -> viewList[position].animateGrowing() }
    }
}




