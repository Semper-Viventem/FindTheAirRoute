package ru.semper_viventem.findtheairroute.ui.map

import me.dmdev.rxpm.map.MapPmExtension
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.ui.common.ScreenPm

class ResultPm(
    private val fromCity: City,
    private val toCity: City
) : ScreenPm(), MapPmExtension {

    override val mapReadyState: MapPmExtension.MapReadyState = MapPmExtension.MapReadyState()


}