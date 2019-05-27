package ru.semper_viventem.findtheairroute.ui.common

import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.navigation.NavigationMessage

abstract class ScreenPm : PresentationModel() {

    fun sendNavigationMessage(message: NavigationMessage) {
        navigationMessages.consumer.accept(message)
    }
}