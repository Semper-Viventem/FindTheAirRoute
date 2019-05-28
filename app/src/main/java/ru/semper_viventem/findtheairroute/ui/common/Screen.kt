package ru.semper_viventem.findtheairroute.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.dmdev.rxpm.base.PmSupportFragment

abstract class Screen<PM : ScreenPm> : PmSupportFragment<PM>(), BackHandler {

    abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView(view)
    }

    override fun handleBack(): Boolean {
        passTo(presentationModel.backAction.consumer)
        return true
    }

    open fun onInitView(view: View) {
        // do nothing
    }
}