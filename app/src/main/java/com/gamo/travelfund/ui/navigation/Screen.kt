package com.gamo.travelfund.ui.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object AddTrip: Screen("add_trip")
    data object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: Long): String {
            return "trip_detail/$tripId"
        }
    }
    data object EditTrip : Screen("edit_trip/{tripId}"){
        fun createRoute(tripId: Long) = "edit_trip/$tripId"
    }

    data object Settings: Screen("settings")
}