package ru.semper_viventem.findtheairroute.ui.changecity

import android.os.Bundle
import kotlinx.android.synthetic.main.screen_change_city.*
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.UIModule
import ru.semper_viventem.findtheairroute.ui.common.Screen

class ChangeCityScreen(args: Bundle) : Screen<ChangeCityPm>(args) {

    companion object {
        private const val KEY_TAG = "key_tag"
        fun newInstance(tag: String) = ChangeCityScreen(Bundle().apply {
            putString(KEY_TAG, tag)
        })
    }

    override val layoutRes: Int = R.layout.screen_change_city

    override fun providePresentationModel(): ChangeCityPm = getKoin()
        .apply { setProperty(UIModule.PROPERTY_TAG, arguments!!.getString(KEY_TAG)) }
        .get()

    override fun onBindPresentationModel(pm: ChangeCityPm) {
        pm.input bindTo input
        pm.cities bindTo { cities ->

        }
    }

}