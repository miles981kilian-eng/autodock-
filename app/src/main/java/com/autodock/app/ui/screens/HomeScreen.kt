package com.autodock.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autodock.app.ui.theme.*
import com.autodock.app.service.DockService
import com.autodock.app.utils.HardwareMonitor
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import com.autodock.app.utils.SystemControls
import android.app.AppOpsManager
import coil.compose.AsyncImage

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var canDrawOverlays by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var hasUsageStats by remember { mutableStateOf(false) }
    
    val prefs = context.getSharedPreferences("AutoDockPrefs", Context.MODE_PRIVATE)
    var spotifyAuto by remember { mutableStateOf(prefs.getBoolean("spotify_auto", true)) }
    
    var batteryPct by remember { mutableIntStateOf(0) }
    var isCharging by remember { mutableStateOf(false) }
    var usedMem by remember { mutableFloatStateOf(0f) }
    var totalMem by remember { mutableFloatStateOf(0f) }
    var storagePct by remember { mutableIntStateOf(0) }
    
    // Live updater
    LaunchedEffect(Unit) {
        while (true) {
            val bat = HardwareMonitor.getBatteryStats(context)
            batteryPct = bat.first
            isCharging = bat.second
            
            val mem = HardwareMonitor.getMemoryStats(context)
            usedMem = mem.first
            totalMem = mem.second
            
            val sto = HardwareMonitor.getStorageStats()
            storagePct = sto.first
            
            delay(5000)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                canDrawOverlays = Settings.canDrawOverlays(context)
                
                val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
                hasUsageStats = mode == AppOpsManager.MODE_ALLOWED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = { CyberBottomNav(navController, "Dashboard") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(navController)
            Spacer(modifier = Modifier.height(24.dp))
            
            HeroStatusBanner()
            Spacer(modifier = Modifier.height(16.dp))
            
            HardwareStatsRow(batteryPct, isCharging, usedMem, totalMem, storagePct)
            Spacer(modifier = Modifier.height(24.dp))
            
            ControlCard(
                icon = Icons.Rounded.Dashboard,
                title = "FLOATING DOCK",
                status = if (canDrawOverlays) "Status: Active" else "Requires Permission",
                statusColor = if (canDrawOverlays) NeonGreen else NeonRed,
                buttonText = if (canDrawOverlays) "OPEN DOCK >" else "AUTHORIZE >",
                onClick = {
                    if (!canDrawOverlays) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                        context.startActivity(intent)
                    } else {
                        context.startService(Intent(context, DockService::class.java))
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ControlCard(
                icon = Icons.Rounded.Memory,
                title = "INTELLIGENCE ENGINE",
                status = if (hasUsageStats) "Active & Learning" else "Needs Usage Access",
                statusColor = if (hasUsageStats) GlowBlue else NeonRed,
                buttonText = if (hasUsageStats) "AI SETTINGS" else "AUTHORIZE >",
                onClick = {
                    if (!hasUsageStats) {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            QuickActionsSection()
            Spacer(modifier = Modifier.height(24.dp))
            
            SmartAutomationsSection(spotifyAuto) { spotifyAuto = it; prefs.edit().putBoolean("spotify_auto", it).apply() }
            Spacer(modifier = Modifier.height(24.dp))
            
            AiPredictionSection()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TopAppBar(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = GlowBlue, modifier = Modifier.size(28.dp))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DarkBackground).border(1.dp, GlowBlue.copy(alpha=0.3f))
            ) {
                DropdownMenuItem(
                    text = { Text("Privacy Policy", color = Color.White) },
                    onClick = { expanded = false; navController.navigate("privacy_policy") }
                )
                DropdownMenuItem(
                    text = { Text("Terms of Service", color = Color.White) },
                    onClick = { expanded = false; navController.navigate("terms") }
                )
                DropdownMenuItem(
                    text = { Text("About AutoDock", color = Color.White) },
                    onClick = { expanded = false; navController.navigate("about") }
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("AUTODOCK OS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("SMART AUTOMATION SYSTEM", color = GlowBlue, fontSize = 9.sp, letterSpacing = 1.sp)
        }
        Icon(Icons.Rounded.Notifications, contentDescription = "Alerts", tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun HeroStatusBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))))
            .border(1.dp, Color.White.copy(alpha=0.1f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            // Glowing Ring Placeholder
            Box(modifier = Modifier.size(70.dp).border(4.dp, GlowBlue, CircleShape), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(50.dp).border(4.dp, GlowPurple.copy(alpha=0.7f), CircleShape))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text("SYSTEM STATUS", color = Subtext, fontSize = 10.sp, letterSpacing = 1.sp)
                Text("OPTIMAL", color = GlowBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("All systems are running smoothly", color = Subtext, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun HardwareStatsRow(bat: Int, charging: Boolean, usedRam: Float, totalRam: Float, storage: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.BatteryFull, title = "BATTERY", value = "$bat%", sub = if (charging) "Charging" else "Discharging")
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.DeveloperBoard, title = "MEMORY", value = String.format("%.1f GB", usedRam), sub = String.format("/ %.1f GB", totalRam))
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Storage, title = "STORAGE", value = "$storage%", sub = "Used")
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, title: String, value: String, sub: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .border(1.dp, Color.White.copy(alpha=0.05f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = GlowBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, color = Subtext, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(sub, color = Subtext, fontSize = 9.sp)
    }
}

@Composable
fun ControlCard(icon: ImageVector, title: String, status: String, statusColor: Color, buttonText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.dp, Color.White.copy(alpha=0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(GlowBlue.copy(alpha=0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = GlowBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(status, color = statusColor, fontSize = 12.sp)
            }
        }
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.border(1.dp, Subtext.copy(alpha=0.3f), RoundedCornerShape(20.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(buttonText, color = GlowBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickActionsSection() {
    val context = LocalContext.current
    Column {
        Text("QUICK ACTIONS", color = Subtext, fontSize = 10.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionIcon(Icons.Rounded.Wifi, "Wi-Fi",
                onClick = { SystemControls.openWifiSettings(context) },
                onLongClick = { SystemControls.openWifiSettings(context) })
            QuickActionIcon(Icons.Rounded.Bluetooth, "Bluetooth",
                onClick = { SystemControls.openBluetoothSettings(context) },
                onLongClick = { SystemControls.openBluetoothSettings(context) })
            QuickActionIcon(Icons.Rounded.FlashlightOn, "Flashlight",
                onClick = { SystemControls.toggleFlashlight(context) },
                onLongClick = { })
            QuickActionIcon(Icons.Rounded.VolumeOff, "Silent Mode",
                onClick = { SystemControls.toggleSilentMode(context) },
                onLongClick = { 
                    val intent = Intent(Settings.ACTION_SOUND_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent) 
                })
            QuickActionIcon(Icons.Rounded.BatterySaver, "Battery Saver",
                onClick = { SystemControls.openBatterySaverSettings(context) },
                onLongClick = { SystemControls.openBatterySaverSettings(context) })
            QuickActionIcon(Icons.Rounded.BrightnessAuto, "Auto Brightness",
                onClick = { SystemControls.toggleAutoBrightness(context) },
                onLongClick = { 
                    val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent) 
                })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuickActionIcon(icon: ImageVector, label: String, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(GlowPurple.copy(alpha=0.4f), Color.Transparent)))
                .border(1.dp, GlowPurple.copy(alpha=0.5f), CircleShape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = GlowBlue)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Subtext, fontSize = 9.sp)
    }
}

@Composable
fun SmartAutomationsSection(spotifyAuto: Boolean, onSpotifyChange: (Boolean) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("SMART AUTOMATIONS", color = Subtext, fontSize = 10.sp, letterSpacing = 1.sp)
            Text("View All", color = GlowBlue, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackground)
                .border(1.dp, Color.White.copy(alpha=0.05f), RoundedCornerShape(20.dp))
        ) {
            AutomationRow(Icons.Rounded.Headphones, "Spotify on Earphones", "When earphones are connected", spotifyAuto, onSpotifyChange, GlowGreen = NeonGreen)
            Divider(color = Color.White.copy(alpha=0.05f))
            AutomationRow(Icons.Rounded.DirectionsCar, "YouTube in Car", "When car Bluetooth is connected", true, {}, GlowGreen = NeonGreen)
            Divider(color = Color.White.copy(alpha=0.05f))
            AutomationRow(Icons.Rounded.School, "Notes at School", "When at school location", true, {}, GlowGreen = NeonGreen)
        }
    }
}

@Composable
fun AutomationRow(icon: ImageVector, title: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, GlowGreen: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontSize = 14.sp)
                Text(desc, color = Subtext, fontSize = 10.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GlowGreen)
        )
    }
}

@Composable
fun AiPredictionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(GlowPurple.copy(alpha=0.2f), CardBackground)))
            .border(1.dp, GlowPurple.copy(alpha=0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Explore, contentDescription = null, tint = GlowBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI PREDICTION", color = GlowBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text("Based on your usage", color = Subtext, fontSize = 10.sp)
            }
            Text("VIEW ALL", color = GlowBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppPredictIcon("Spotify", "94%", GlowGreen = NeonGreen, url = "https://logo.clearbit.com/spotify.com", packageName = "com.spotify.music")
            AppPredictIcon("Instagram", "82%", GlowGreen = Color(0xFFE1306C), url = "https://logo.clearbit.com/instagram.com", packageName = "com.instagram.android")
            AppPredictIcon("YouTube", "75%", GlowGreen = NeonRed, url = "https://logo.clearbit.com/youtube.com", packageName = "com.google.android.youtube")
            AppPredictIcon("Chrome", "60%", GlowGreen = Color(0xFFF4B400), url = "https://logo.clearbit.com/google.com", packageName = "com.android.chrome")
            AppPredictIcon("WhatsApp", "42%", GlowGreen = NeonGreen, url = "https://logo.clearbit.com/whatsapp.com", packageName = "com.whatsapp")
        }
    }
}

@Composable
fun AppPredictIcon(name: String, probability: String, GlowGreen: Color, url: String, packageName: String) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    context.startActivity(intent)
                } else {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                    context.startActivity(webIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(GlowGreen),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = name,
                modifier = Modifier.fillMaxSize().padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, color = Subtext, fontSize = 9.sp)
        Text(probability, color = GlowBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CyberBottomNav(navController: NavController, currentScreen: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Rounded.Home, "Dashboard", currentScreen == "Dashboard") { navController.navigate("dashboard") }
            BottomNavItem(Icons.Rounded.Bolt, "Automations", currentScreen == "Automations") { navController.navigate("automations") }
            
            // Center Floating Action Button (Dock)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-10).dp)
                    .shadow(16.dp, CircleShape, spotColor = GlowBlue)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GlowBlue, Color(0xFF0055FF))))
                    .border(2.dp, GlowBlue.copy(alpha=0.5f), CircleShape)
                    .clickable { /* Toggles Smart Hub (to be implemented) */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Apps, contentDescription = "Dock", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            
            BottomNavItem(Icons.Rounded.GridView, "Dock", currentScreen == "Dock") { navController.navigate("dock") }
            BottomNavItem(Icons.Rounded.Settings, "Settings", currentScreen == "Settings") { navController.navigate("settings") }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Icon(icon, contentDescription = label, tint = if (selected) GlowBlue else Subtext, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = if (selected) GlowBlue else Subtext, fontSize = 9.sp)
    }
}
