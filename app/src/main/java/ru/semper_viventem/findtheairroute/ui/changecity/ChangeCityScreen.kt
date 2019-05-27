package ru.semper_viventem.findtheairroute.ui.changecity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.support.v7.widget.navigationClicks
import kotlinx.android.synthetic.main.screen_change_city.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.exchangerates.extensions.showKeyboard
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.UIModule
import ru.semper_viventem.findtheairroute.ui.common.Screen

class ChangeCityScreen : Screen<ChangeCityPm>() {

    companion object {
        private const val KEY_TAG = "key_tag"
        fun newInstance(tag: String) = ChangeCityScreen().apply {
            arguments = Bundle().apply {
                putString(KEY_TAG, tag)
            }
        }
    }

    override val layoutRes: Int = R.layout.screen_change_city

    private val citiesAdapter = CitiesAdapter { city ->
        city passTo presentationModel.cityChanged
    }

    override fun onInitView(view: View) {
        super.onInitView(view)

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = citiesAdapter
            setHasFixedSize(true)
        }

        with(input) {
            post { showKeyboard() }
        }
    }

    override fun providePresentationModel(): ChangeCityPm = getKoin()
        .apply { setProperty(UIModule.PROPERTY_TAG, arguments!!.getString(KEY_TAG)) }
        .get()

    override fun onBindPresentationModel(pm: ChangeCityPm) {
        pm.input bindTo input
        pm.cities bindTo citiesAdapter::setItems

        toolbar.navigationClicks() bindTo pm.backAction
    }

}