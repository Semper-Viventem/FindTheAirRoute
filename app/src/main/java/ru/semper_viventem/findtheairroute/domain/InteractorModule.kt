package ru.semper_viventem.findtheairroute.domain

import org.koin.dsl.module

object InteractorModule {
    val module = module {
        factory { FindCitiesByNameInteractor(get()) }
    }
}