package ru.semper_viventem.findtheairroute.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import me.dmdev.rxpm.base.PmSupportActivity
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationMessageHandler
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.exchangerates.extensions.hideKeyboard
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.Back
import ru.semper_viventem.findtheairroute.ui.CityChanged
import ru.semper_viventem.findtheairroute.ui.OpenChangeCityScreen
import ru.semper_viventem.findtheairroute.ui.OpenHomeScreen
import ru.semper_viventem.findtheairroute.ui.changecity.ChangeCityScreen
import ru.semper_viventem.findtheairroute.ui.common.Screen
import ru.semper_viventem.findtheairroute.ui.home.HomeScreen

class MainActivity : PmSupportActivity<MainPm>(), NavigationMessageHandler {

    override fun providePresentationModel(): MainPm = getKoin().get()

    private val containerId = R.id.container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBindPresentationModel(pm: MainPm) {
        // do nothing
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        container.hideKeyboard()

        when (message) {
            is Back -> back()
            is OpenHomeScreen -> setRootScreen(HomeScreen())
            is OpenChangeCityScreen -> openScreen(ChangeCityScreen.newInstance(message.tag))
            is CityChanged -> backTo<HomeScreen>()?.onCityChanged(message.tag, message.city)
        }
        return true
    }

    private fun openScreen(screen: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(containerId, screen)
            .addToBackStack(screen::class.java.canonicalName)
            .commit()
    }

    override fun onBackPressed() {
        if ((supportFragmentManager.fragments.firstOrNull() as? Screen<*>)?.handleBack()?.not() == true) {
            super.onBackPressed()
        }
    }

    private fun setRootScreen(screen: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, screen)
            .commit()
    }

    private fun back() {
        if (!supportFragmentManager.popBackStackImmediate()) finish()
    }

    private inline fun <reified T : Fragment> backTo(): T? {
        supportFragmentManager.popBackStack()
        return supportFragmentManager.fragments.firstOrNull() as? T
    }
}
