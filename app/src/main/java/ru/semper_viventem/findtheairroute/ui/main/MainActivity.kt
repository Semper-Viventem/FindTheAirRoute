package ru.semper_viventem.findtheairroute.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import me.dmdev.rxpm.base.PmSupportActivity
import me.dmdev.rxpm.navigation.NavigationMessage
import me.dmdev.rxpm.navigation.NavigationMessageHandler
import org.koin.android.ext.android.getKoin
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.ui.OpenHomeScreen
import ru.semper_viventem.findtheairroute.ui.home.HomeScreen

class MainActivity : PmSupportActivity<MainPm>(), NavigationMessageHandler {

    override fun providePresentationModel(): MainPm = getKoin().get()

    private val containerId = R.id.container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleNavigationMessage(OpenHomeScreen())
    }

    override fun onBindPresentationModel(pm: MainPm) {
        // do nothing
    }

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when(message) {
            is OpenHomeScreen -> setRoot(HomeScreen())
        }

        return true
    }

    private fun openScreen(screen: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager
            .beginTransaction()
            .add(containerId, screen)
            .apply {
                if (addToBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    private fun setRoot(screen: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, screen)
            .commit()
    }
}
