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
import com.autodock.app.ui.theme.CyberBlue
import com.autodock.app.ui.theme.DeepGraphite
import com.autodock.app.ui.theme.GlassSurface
import com.autodock.app.service.DockService
import com.autodock.app.utils.HardwareMonitor
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

// Expanded Palette for the new Mockup
val DarkBackground = Color(0xFF03050B)
val CardBackground = Color(0xFF0A0E17)
val GlowBlue = Color(0xFF00F0FF)
val GlowPurple = Color(0xFF7B2CBF)
val NeonGreen = Color(0xFF00E676)
val NeonRed = Color(0xFFFF1744)
val Subtext = Color(0xFF6B7280)

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var canDrawOverlays by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    
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
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = { CyberBottomNav() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar()
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
                status = "Active & Learning",
                statusColor = GlowBlue,
                buttonText = "AI SETTINGS",
                onClick = {}
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
fun TopAppBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = GlowBlue, modifier = Modifier.size(28.dp))
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
    Column {
        Text("QUICK ACTIONS", color = Subtext, fontSize = 10.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionIcon(Icons.Rounded.Wifi, "Wi-Fi")
            QuickActionIcon(Icons.Rounded.Bluetooth, "Bluetooth")
            QuickActionIcon(Icons.Rounded.FlashlightOn, "Flashlight")
            QuickActionIcon(Icons.Rounded.VolumeOff, "Silent Mode")
            QuickActionIcon(Icons.Rounded.BatterySaver, "Battery Saver")
            QuickActionIcon(Icons.Rounded.BrightnessAuto, "Auto Brightness")
        }
    }
}

@Composable
fun QuickActionIcon(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(GlowPurple.copy(alpha=0.4f), Color.Transparent)))
                .border(1.dp, GlowPurple.copy(alpha=0.5f), CircleShape),
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
            AppPredictIcon("Spotify", GlowGreen = NeonGreen)
            AppPredictIcon("Instagram", GlowGreen = Color(0xFFE1306C))
            AppPredictIcon("YouTube", GlowGreen = NeonRed)
            AppPredictIcon("Chrome", GlowGreen = Color(0xFFF4B400))
            AppPredictIcon("WhatsApp", GlowGreen = NeonGreen)
        }
    }
}

@Composable
fun AppPredictIcon(name: String, GlowGreen: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(GlowGreen),
            contentAlignment = Alignment.Center
        ) {
            // Stand-in for actual app icons
            Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, color = Subtext, fontSize = 9.sp)
    }
}

@Composable
fun CyberBottomNav() {
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
            BottomNavItem(Icons.Rounded.Home, "Dashboard", true)
            BottomNavItem(Icons.Rounded.Bolt, "Automations", false)
            
            // Center Floating Action Button (Dock)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(y = (-10).dp)
                    .shadow(16.dp, CircleShape, spotColor = GlowBlue)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GlowBlue, Color(0xFF0055FF))))
                    .border(2.dp, GlowBlue.copy(alpha=0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Apps, contentDescription = "Dock", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            
            BottomNavItem(Icons.Rounded.GridView, "Dock", false)
            BottomNavItem(Icons.Rounded.Settings, "Settings", false)
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = if (selected) GlowBlue else Subtext, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = if (selected) GlowBlue else Subtext, fontSize = 9.sp)
    }
}
