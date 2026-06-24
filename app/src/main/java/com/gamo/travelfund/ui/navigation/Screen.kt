package com.gamo.travelfund.ui.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object AddTrip: Screen("add_trip")
    data object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: Long): String {
            return "trip_detail/$tripId"
        }
    }
}