package ru.semper_viventem.findtheairroute.ui.map

import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.ui.common.MapScreenPm

class ResultPm(
    private val fromCity: City,
    private val toCity: City
) : MapScreenPm() {

    /**
     * Pair with [fromCity] and [toCity]
     */
    val points = State<Pair<City, City>>()

    override fun onCreate() {
        super.onCreate()

        mapReady.observable
            .filter { it }
            .map { fromCity to toCity }
            .subscribe(points.consumer)
            .untilDestroy()
    }
}