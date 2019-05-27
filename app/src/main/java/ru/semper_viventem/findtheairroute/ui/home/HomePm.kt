package ru.semper_viventem.findtheairroute.ui.home

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.ui.OpenChangeCityScreen
import ru.semper_viventem.findtheairroute.ui.common.ScreenPm

class HomePm : ScreenPm() {

    companion object {
        private const val TAG_FROM_CITY = "from_city"
        private const val TAG_TO_CITY = "to_city"
    }

    class CityValue(
        val city: City? = null
    )

    val fromCity = State(CityValue())
    val toCity = State(CityValue())
    val searchButtonEnabled = State(false)

    val fromCityClicks = Action<Unit>()
    val toCityClicks = Action<Unit>()

    /**
     * Pair with tag and changed city
     */
    val cityChanged = Action<Pair<String, City>>()

    override fun onCreate() {
        super.onCreate()

        fromCityClicks.observable
            .subscribe { sendNavigationMessage(OpenChangeCityScreen(TAG_FROM_CITY)) }
            .untilDestroy()

        toCityClicks.observable
            .subscribe { sendNavigationMessage(OpenChangeCityScreen(TAG_TO_CITY)) }
            .untilDestroy()

        cityChanged.observable
            .subscribe { (tag, city) ->
                when (tag) {
                    TAG_FROM_CITY -> fromCity.consumer.accept(CityValue(city))
                    TAG_TO_CITY -> toCity.consumer.accept(CityValue(city))
                }
            }
            .untilDestroy()

        Observable.combineLatest(
            fromCity.observable,
            toCity.observable,
            BiFunction { fromCityValue: CityValue, toCityValue: CityValue ->
                fromCityValue.city != null && toCityValue.city != null
            }
        )
            .subscribe(searchButtonEnabled.consumer)
            .untilDestroy()
    }
}