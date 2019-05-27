package ru.semper_viventem.findtheairroute.ui.home

import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.screen_home.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.common.Screen

class HomeScreen : Screen<HomePm>() {

    override val layoutRes: Int = R.layout.screen_home

    override fun providePresentationModel(): HomePm = getKoin().get()

    override fun onBindPresentationModel(pm: HomePm) {
        pm.fromCity bindTo { cityValue ->
            fromInput.editText?.setText(cityValue.city?.cityName.orEmpty())
        }
        pm.toCity bindTo { cityValue ->
            toInput.editText?.setText(cityValue.city?.cityName.orEmpty())
        }
        pm.searchButtonEnabled bindTo searchButton::setEnabled
        fromEdit.clicks() bindTo pm.fromCityClicks
        toEdit.clicks() bindTo pm.toCityClicks
    }
}