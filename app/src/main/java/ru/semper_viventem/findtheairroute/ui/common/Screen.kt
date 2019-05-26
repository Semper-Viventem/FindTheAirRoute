package ru.semper_viventem.findtheairroute.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.dmdev.rxpm.base.PmSupportFragment

abstract class Screen<T: ScreenPm> : PmSupportFragment<T> {

    constructor() : super()

    constructor(args: Bundle) : super() {
        arguments = args
    }

    abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView(view)
    }

    open fun onInitView(view: View) {
        // do nothing
    }
}