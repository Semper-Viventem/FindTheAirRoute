package ru.semper_viventem.findtheairroute.ui.changecity

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.widget.dialogControl
import me.dmdev.rxpm.widget.inputControl
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.domain.FindCitiesByNameInteractor
import ru.semper_viventem.findtheairroute.ui.CityChanged
import ru.semper_viventem.findtheairroute.ui.common.ScreenPm
import timber.log.Timber
import java.net.UnknownHostException

class ChangeCityPm(
    private val tag: String,
    private val findCitiesByNameInteractor: FindCitiesByNameInteractor
) : ScreenPm() {

    companion object {
        private const val MIN_INPUT_SYMBOLS = 3
    }

    private var searchDisposable: Disposable? = null

    val input = inputControl()
    val errorDialog = dialogControl<Int, Unit>()
    val cities = State<List<City>>()
    val progress = State(false)
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
            .subscribe(cities.consumer, Consumer { error ->
                val message = when (error) {
                    is UnknownHostException -> R.string.error_no_internet_connection
                    else -> R.string.error_loading
                }
                errorDialog.show(message)
                Timber.e(error)
            })
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