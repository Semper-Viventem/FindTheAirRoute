package ru.semper_viventem.findtheairroute.ui.changecity

import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.bindProgress
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

    private var searchDisposable: Disposable? = null

    val input = inputControl()
    val cities = State<List<City>>()
    val progress = State(false)
    val errorMessage = State<Int>()
    val cityChanged = Action<City>()

    override fun onCreate() {
        super.onCreate()

        input.textChanges.observable
            .filter { it.length >= MIN_INPUT_SYMBOLS }
            .subscribe(::search)
            .untilDestroy()

        cityChanged.observable
            .subscribe { sendNavigationMessage(CityChanged(tag, it)) }
            .untilDestroy()
    }

    private fun search(query: String) {
        searchDisposable?.dispose()
        findCitiesByNameInteractor.execute(query)
            .bindProgress(progress.consumer)
            .doOnSuccess(cities.consumer)
            .doOnError { Timber.e(it) }
            .retry()
            .subscribe()
            .untilSearch()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchDisposable?.dispose()
        searchDisposable = null
    }

    private fun Disposable.untilSearch() {
        searchDisposable = this
    }
}