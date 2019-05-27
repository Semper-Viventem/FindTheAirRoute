package ru.semper_viventem.findtheairroute.ui

import org.koin.dsl.module
import ru.semper_viventem.findtheairroute.ui.changecity.ChangeCityPm
import ru.semper_viventem.findtheairroute.ui.home.HomePm
import ru.semper_viventem.findtheairroute.ui.main.MainPm

object UIModule {

    const val PROPERTY_TAG = "property_tag"

    val module = module {
        factory { MainPm() }
        factory { HomePm() }
        factory { ChangeCityPm(getProperty(PROPERTY_TAG), get()) }
    }
}