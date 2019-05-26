package ru.semper_viventem.findtheairroute.ui

import org.koin.dsl.module
import ru.semper_viventem.findtheairroute.ui.main.MainPm

object UIModule {
    val module = module {
        factory { MainPm() }
    }
}