package com.autodock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.autodock.app.ui.screens.HomeScreen
import com.autodock.app.ui.theme.AutoDockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoDockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = androidx.navigation.compose.rememberNavController()
                    androidx.navigation.compose.NavHost(navController = navController, startDestination = "dashboard") {
                        androidx.navigation.compose.composable("dashboard") { HomeScreen(navController) }
                        androidx.navigation.compose.composable("automations") { com.autodock.app.ui.screens.AutomationsScreen(navController) }
                        androidx.navigation.compose.composable("dock") { com.autodock.app.ui.screens.DockSettingsScreen(navController) }
                        androidx.navigation.compose.composable("settings") { com.autodock.app.ui.screens.SettingsScreen(navController) }
                        androidx.navigation.compose.composable("about") { com.autodock.app.ui.screens.AboutScreen(navController) }
                    }
                }
            }
        }
    }
}
