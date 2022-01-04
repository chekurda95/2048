package com.chekurda.game_2048.screens.game.presentation.gamefield.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.contract.GameFragmentFactory
import com.chekurda.game_2048.screens.game.presentation.gamefield.delegates.SwipeControllerDelegate
import com.chekurda.game_2048.screens.game.presentation.gamefield.delegates.SwipeDelegate
import com.chekurda.game_2048.screens.game.presentation.gamefield.presenter.GamePresenter
import com.chekurda.game_2048.screens.game.presentation.gamefield.presenter.IGamePresenter
import com.chekurda.game_2048.screens.game.presentation.views.cell.CellView

@SuppressLint("ClickableViewAccessibility")
internal class GameFragment : Fragment(),
    IGameFragment,
    SwipeDelegate by SwipeControllerDelegate() {

    companion object : GameFragmentFactory {

        override fun createGameFragment() =
            GameFragment()
    }

    private val gamePresenter: IGamePresenter by lazy { ViewModelProvider(this)[GamePresenter::class.java] }

    private lateinit var viewList: List<CellView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_field, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gamePresenter.attachView(this)
        viewList = listOf(
            getCell(R.id.cell_0), getCell(R.id.cell_1), getCell(R.id.cell_2), getCell(R.id.cell_3),
            getCell(R.id.cell_4), getCell(R.id.cell_5), getCell(R.id.cell_6), getCell(R.id.cell_7),
            getCell(R.id.cell_8), getCell(R.id.cell_9), getCell(R.id.cell_10), getCell(R.id.cell_11),
            getCell(R.id.cell_12), getCell(R.id.cell_13), getCell(R.id.cell_14), getCell(R.id.cell_15)
        )
        gamePresenter.startNewGame()
        initSwipeDelegate(requireView().findViewById(R.id.game_field), gamePresenter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gamePresenter.detachView()
    }

    private fun getCell(@IdRes id: Int): CellView =
        requireView().findViewById(id)

    override fun drawField(listOfCellsValues: List<Int>) {
        viewList.forEachIndexed { index, cellView -> cellView.setValue(listOfCellsValues[index]) }
    }

    override fun animateField(movedList: List<Int>) {
        movedList.forEach { position -> viewList[position].animateGrowing() }
    }
}




