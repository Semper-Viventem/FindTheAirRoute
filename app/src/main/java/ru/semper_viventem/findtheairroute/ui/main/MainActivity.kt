package ru.semper_viventem.findtheairroute.ui.main

import android.os.Bundle
import me.dmdev.rxpm.base.PmSupportActivity
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R

class MainActivity : PmSupportActivity<MainPm>() {

    override fun providePresentationModel(): MainPm = getKoin().get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBindPresentationModel(pm: MainPm) {
        // TODO implement navigation
    }
}
