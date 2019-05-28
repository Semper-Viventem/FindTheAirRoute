package ru.semper_viventem.findtheairroute.ui.common

abstract class MapScreenPm : ScreenPm() {

    val mapReady = State(false)
    val mapReadyAction = Action<Boolean>()

    override fun onCreate() {
        super.onCreate()

        mapReadyAction.observable
            .subscribe(mapReady.consumer)
            .untilDestroy()
    }
}