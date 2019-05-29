package ru.semper_viventem.findtheairroute.ui.home

import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.screen_home.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City
import ru.semper_viventem.findtheairroute.ui.common.Screen

class HomeScreen : Screen<HomePm>() {

    override val layoutRes: Int = R.layout.screen_home

    override fun providePresentationModel(): HomePm = getKoin().get()

    override fun onBindPresentationModel(pm: HomePm) {
        pm.fromCity bindTo { cityValue ->
            fromButton.text = cityValue.city?.fullName.orEmpty()
        }
        pm.toCity bindTo { cityValue ->
            toButton.text = cityValue.city?.fullName.orEmpty()
        }
        pm.searchButtonEnabled bindTo searchButton::setEnabled

        searchButton.clicks() bindTo pm.searchButtonClicks
        fromButton.clicks() bindTo  pm.fromCityClicks
        toButton.clicks() bindTo pm.toCityClicks
    }

    fun onCityChanged(tag: String, city: City) {
        tag to city passTo presentationModel.cityChanged
    }
}