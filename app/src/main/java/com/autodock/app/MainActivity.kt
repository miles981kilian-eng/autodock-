package com.autodock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "dashboard") {
                        composable("dashboard") { HomeScreen(navController) }
                        composable("automations") { com.autodock.app.ui.screens.AutomationsScreen(navController) }
                        composable("dock") { com.autodock.app.ui.screens.DockSettingsScreen(navController) }
                        composable("settings") { com.autodock.app.ui.screens.SettingsScreen(navController) }
                        composable("about") { com.autodock.app.ui.screens.AboutScreen(navController) }
                        composable("privacy_policy") { com.autodock.app.ui.screens.PrivacyPolicyScreen(navController) }
                        composable("terms") { com.autodock.app.ui.screens.TermsOfServiceScreen(navController) }
                    }
                }
            }
        }
    }
}
