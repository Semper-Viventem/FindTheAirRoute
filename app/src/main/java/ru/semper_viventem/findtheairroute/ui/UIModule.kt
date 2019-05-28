package ru.semper_viventem.findtheairroute.ui

import org.koin.dsl.module
import ru.semper_viventem.findtheairroute.ui.changecity.ChangeCityPm
import ru.semper_viventem.findtheairroute.ui.home.HomePm
import ru.semper_viventem.findtheairroute.ui.main.MainPm
import ru.semper_viventem.findtheairroute.ui.map.ResultPm

object UIModule {

    const val PROPERTY_TAG = "property_tag"
    const val PROPERTY_FROM_CITY = "property_from_city"
    const val PROPERTY_TO_CITY = "property_to_city"

    val module = module {
        factory { MainPm() }
        factory { HomePm() }
        factory { ChangeCityPm(getProperty(PROPERTY_TAG), get()) }
        factory { ResultPm(getProperty(PROPERTY_FROM_CITY), getProperty(PROPERTY_TO_CITY)) }
    }
}