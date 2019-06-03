package ru.semper_viventem.findtheairroute.ui.map

import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.ui.common.MapScreenPm

class ResultPm(
    private val fromCity: City,
    private val toCity: City
) : MapScreenPm() {

    data class ScreenState(
        val from: City,
        val to: City,
        val startPosition: Float,
        val endPosition: Float
    )

    val state = State<ScreenState>()
    private val animationProgress = State(0F to 1F)

    /**
     * Current animation value to and animation value
     */
    val saveProgress = Action<Pair<Float, Float>>()

    override fun onCreate() {
        super.onCreate()

        mapReady.observable
            .filter { it }
            .map { ScreenState(fromCity, toCity, animationProgress.value.first, animationProgress.value.second) }
            .subscribe(state.consumer)
            .untilDestroy()

        saveProgress.observable
            .subscribe(animationProgress.consumer)
            .untilDestroy()
    }
}