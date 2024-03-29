package com.chekurda.common.base_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.chekurda.common.base_fragment.util.ArchDelegate

abstract class BasePresenterFragment<VIEW, PRESENTER : BasePresenter<VIEW>> :  Fragment() {

    @get:LayoutRes
    abstract val layoutRes: Int

    /**
     * Должен создаваться НОВЫЙ объект презентера, с которым будет работать Activity.
     * Метод вызывается единожды, как только Activity создана.
     * После этого, для использования самого объекта презентера необходимо использовать [presenter]
     * @return PRESENTER объект презентера
     */
    protected abstract fun createPresenter(): PRESENTER

    /**
     * Должен предоставить вью для прикрепления к презентеру, как правило, передается указатель на текущий фрагмент
     * @return VIEW
     */
    protected abstract fun getPresenterView(): VIEW

    @Suppress("LeakingThis")
    protected val presenter: PRESENTER by ArchDelegate(
        fragment = this,
        creatingMethod = ::createPresenter,
        doOnCleared = { it.onDestroy() }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        createView(inflater, container)

    open fun createView(inflater: LayoutInflater, container: ViewGroup?): View =
        inflater.inflate(layoutRes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachView(getPresenterView())
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }
}