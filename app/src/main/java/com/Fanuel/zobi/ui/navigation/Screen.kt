package com.Fanuel.zobi.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dialer : Screen("dialer")
    object Contacts : Screen("contacts")
    object CallHistory : Screen("call_history")
    object Settings : Screen("settings")
    object Calling : Screen("calling")
}
