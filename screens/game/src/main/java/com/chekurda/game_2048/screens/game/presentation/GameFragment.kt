package com.chekurda.game_2048.screens.game.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.chekurda.common.base_fragment.BasePresenterFragment
import com.chekurda.game_2048.screens.game.R
import com.chekurda.game_2048.screens.game.contract.GameFragmentFactory
import com.chekurda.game_2048.screens.game.domain.GameController
import com.chekurda.game_2048.screens.game.presentation.delegates.SwipeControllerDelegate
import com.chekurda.game_2048.screens.game.presentation.delegates.SwipeDelegate
import com.chekurda.game_2048.screens.game.presentation.views.GameView
import com.chekurda.game_2048.screens.game.presentation.views.cell.CellView

@SuppressLint("ClickableViewAccessibility")
internal class GameFragment : BasePresenterFragment<GameFragmentView, GamePresenter>(),
    GameFragmentView,
    SwipeDelegate by SwipeControllerDelegate() {

    companion object : GameFragmentFactory {

        override fun createGameFragment() = GameFragment()
    }

    override val layoutRes: Int = -1

    /* private lateinit var viewList: List<CellView> */

    private var gameView: GameView? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View =
        GameView(requireContext()).also { view ->
            view.id = R.id.game_view
            gameView = view
        }

    override fun attachGameController(controller: GameController) {
        gameView!!.attachGameController(controller)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameView = null
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewList = listOf(
            getCell(R.id.cell_0), getCell(R.id.cell_1), getCell(R.id.cell_2), getCell(R.id.cell_3),
            getCell(R.id.cell_4), getCell(R.id.cell_5), getCell(R.id.cell_6), getCell(R.id.cell_7),
            getCell(R.id.cell_8), getCell(R.id.cell_9), getCell(R.id.cell_10), getCell(R.id.cell_11),
            getCell(R.id.cell_12), getCell(R.id.cell_13), getCell(R.id.cell_14), getCell(R.id.cell_15)
        )
        presenter.startNewGame()
        initSwipeDelegate(requireView().findViewById(R.id.game_field), presenter)

        val cell = CellView2(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(200, 200)
            value = 32
        }
        (view as ViewGroup).addView(cell)
    }*/

    private fun getCell(@IdRes id: Int): CellView =
        requireView().findViewById(id)

    override fun drawField(listOfCellsValues: List<Int>) {
        /*viewList.forEachIndexed { index, cellView -> cellView.setValue(listOfCellsValues[index]) }*/
    }

    override fun animateField(movedList: List<Int>) {
        /*movedList.forEach { position -> viewList[position].animateGrowing() }*/
    }

    override fun createPresenter(): GamePresenter = GamePresenterImpl()

    override fun getPresenterView(): GameFragmentView = this
}




