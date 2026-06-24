package com.gamo.travelfund

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gamo.travelfund.ui.navigation.AppNavigation
import com.gamo.travelfund.ui.theme.TravelFundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TravelFundTheme {
                AppNavigation()
            }
        }
    }
}