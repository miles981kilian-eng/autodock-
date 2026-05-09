package com.autodock.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.autodock.app.ui.theme.CyberBlue
import com.autodock.app.ui.theme.CardBackground
import com.autodock.app.service.DockService

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var canDrawOverlays by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    
    val prefs = context.getSharedPreferences("AutoDockPrefs", Context.MODE_PRIVATE)
    var spotifyAuto by remember { mutableStateOf(prefs.getBoolean("spotify_auto", true)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AutoDock",
            style = MaterialTheme.typography.headlineLarge,
            color = CyberBlue,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // System Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("System Status", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!canDrawOverlays) {
                    Text("Permission Required: Display over other apps", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                    ) {
                        Text("Grant Permission", color = MaterialTheme.colorScheme.onPrimary)
                    }
                } else {
                    Text("All Permissions Granted", color = CyberBlue, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, DockService::class.java)
                            context.startService(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                    ) {
                        Text("Start Dock Service", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Smart Automations Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Smart Automations", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Launch Spotify", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        Text("When headphones connect", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = spotifyAuto,
                        onCheckedChange = { isChecked ->
                            spotifyAuto = isChecked
                            prefs.edit().putBoolean("spotify_auto", isChecked).apply()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberBlue, checkedTrackColor = CyberBlue.copy(alpha=0.5f))
                    )
                }
            }
        }
    }
}
