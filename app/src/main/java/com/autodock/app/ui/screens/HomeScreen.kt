package com.autodock.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autodock.app.ui.theme.CyberBlue
import com.autodock.app.ui.theme.DeepGraphite
import com.autodock.app.ui.theme.GlassSurface
import com.autodock.app.service.DockService

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var canDrawOverlays by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    
    val prefs = context.getSharedPreferences("AutoDockPrefs", Context.MODE_PRIVATE)
    var spotifyAuto by remember { mutableStateOf(prefs.getBoolean("spotify_auto", true)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                canDrawOverlays = Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGraphite)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "AUTODOCK OS",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "VERSION 2.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = CyberBlue
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Premium Glass Card for Dock Control
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(GlassSurface)
                .border(1.dp, CyberBlue.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text("FLOATING DOCK", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (!canDrawOverlays) {
                    Text("REQ: DISPLAY_OVER_APPS", color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    ) {
                        Text("AUTHORIZE", color = CyberBlue, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    Text("STATUS: ACTIVE", color = CyberBlue, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, DockService::class.java)
                            context.startService(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                    ) {
                        Text("INITIALIZE DOCK", color = DeepGraphite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Smart Automations Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(GlassSurface)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha=0.1f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text("INTELLIGENCE", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AUTO_LAUNCH_SPOTIFY", color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Text("Trigger: BLUETOOTH_CONNECT", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    Switch(
                        checked = spotifyAuto,
                        onCheckedChange = { isChecked ->
                            spotifyAuto = isChecked
                            prefs.edit().putBoolean("spotify_auto", isChecked).apply()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = CyberBlue, checkedTrackColor = CyberBlue.copy(alpha=0.2f))
                    )
                }
            }
        }
    }
}
