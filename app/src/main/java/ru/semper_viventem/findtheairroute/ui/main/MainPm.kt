package ru.semper_viventem.findtheairroute.ui.main

import ru.semper_viventem.findtheairroute.ui.OpenHomeScreen
import ru.semper_viventem.findtheairroute.ui.common.ScreenPm

class MainPm : ScreenPm() {
    override fun onCreate() {
        super.onCreate()

        sendNavigationMessage(OpenHomeScreen())
    }
}