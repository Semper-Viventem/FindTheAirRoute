package ru.semper_viventem.findtheairroute.ui

import me.dmdev.rxpm.navigation.NavigationMessage
import ru.semper_viventem.findtheairroute.domain.City

class Back() : NavigationMessage
class OpenHomeScreen : NavigationMessage
class OpenChangeCityScreen(val tag: String) : NavigationMessage
class ScreenChanged(val tag: String, val city: City) : NavigationMessage
class OpenMap(val from: City, val to: City) : NavigationMessage