package ru.semper_viventem.findtheairroute.ui.changecity

import me.dmdev.rxpm.widget.inputControl
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.domain.FindCitiesByNameInteractor
import ru.semper_viventem.findtheairroute.ui.CityChanged
import ru.semper_viventem.findtheairroute.ui.common.ScreenPm
import timber.log.Timber

class ChangeCityPm(
    private val tag: String,
    private val findCitiesByNameInteractor: FindCitiesByNameInteractor
) : ScreenPm() {

    companion object {
        private const val MIN_INPUT_SYMBOLS = 3
    }

    val input = inputControl()
    val cities = State<List<City>>()
    val cityChanged = Action<City>()

    override fun onCreate() {
        super.onCreate()

        input.textChanges.observable
            .filter { it.length < MIN_INPUT_SYMBOLS }
            .flatMapSingle { query ->
                findCitiesByNameInteractor.execute(query)
            }
            .doOnNext(cities.consumer)
            .doOnError { Timber.e(it) }
            .retry()
            .subscribe()
            .untilDestroy()

        cityChanged.observable
            .subscribe { sendNavigationMessage(CityChanged(tag, it)) }
            .untilDestroy()
    }
}