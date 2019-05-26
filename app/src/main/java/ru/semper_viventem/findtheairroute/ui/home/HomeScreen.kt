package ru.semper_viventem.findtheairroute.ui.home

import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.common.Screen

class HomeScreen: Screen<HomePm>() {

    override val layoutRes: Int = R.layout.screen_home

    override fun providePresentationModel(): HomePm = getKoin().get()

    override fun onBindPresentationModel(pm: HomePm) {

    }
}