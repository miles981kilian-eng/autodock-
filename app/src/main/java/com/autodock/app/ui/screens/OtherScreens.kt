package com.autodock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.autodock.app.ui.theme.CyberBlue
import com.autodock.app.ui.theme.DarkBackground
import com.autodock.app.ui.theme.GlowBlue
import com.autodock.app.ui.theme.Subtext

@Composable
fun AutomationsScreen(navController: NavController) {
    Scaffold(containerColor = DarkBackground, bottomBar = { CyberBottomNav(navController, "Automations") }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AUTOMATIONS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = GlowBlue)) {
                    Text("+ Create Automation", color = DarkBackground)
                }
            }
        }
    }
}

@Composable
fun DockSettingsScreen(navController: NavController) {
    Scaffold(containerColor = DarkBackground, bottomBar = { CyberBottomNav(navController, "Dock") }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("DOCK CONTROLS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(containerColor = DarkBackground, bottomBar = { CyberBottomNav(navController, "Settings") }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ADVANCED SETTINGS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { navController.navigate("about") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                    Text("About AutoDock >", color = GlowBlue)
                }
            }
        }
    }
}

@Composable
fun AboutScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(32.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            Text("AUTODOCK OS", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Text("THE FUTURE OF ANDROID AUTOMATION", color = GlowBlue, fontSize = 10.sp, letterSpacing = 2.sp)
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Text("AutoDock is a futuristic edge automation system designed to minimize friction and enhance your productivity.", color = Subtext, textAlign = TextAlign.Center, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Positive Impact", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Faster access to tools\n• Smarter device interactions\n• Reduced manual actions\n• Personalized AI assistance", color = GlowBlue, textAlign = TextAlign.Center, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text("Designed & Produced By", color = Subtext, fontSize = 12.sp, letterSpacing = 2.sp)
            Text("KILIAN OCHIENG", color = GlowBlue, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("< BACK", color = GlowBlue)
            }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(24.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("PRIVACY POLICY", color = GlowBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable content
            Column(modifier = Modifier.weight(1f).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                Text("Last Updated: June 2026", color = Subtext, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("1. Information We Collect", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("AutoDock collects usage statistics to power the Intelligence Engine and predict your next actions. We only collect the minimal required data (such as app usage frequency and device state like Bluetooth/Wi-Fi connection status).", color = Subtext, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("2. How We Use Your Information", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("The information collected is strictly processed locally on your device to trigger automated workflows and is never sold or shared with third parties.", color = Subtext, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("3. Permissions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("AutoDock requires the 'Display over other apps' permission to show the floating smart dock, and 'Usage Access' to analyze which apps to suggest next.", color = Subtext, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("< BACK", color = GlowBlue)
            }
        }
    }
}

@Composable
fun TermsOfServiceScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground).padding(24.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("TERMS OF SERVICE", color = GlowBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable content
            Column(modifier = Modifier.weight(1f).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                Text("Last Updated: June 2026", color = Subtext, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("1. Acceptance of Terms", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("By downloading and using AutoDock, you agree to these Terms of Service. If you do not agree, please uninstall the application.", color = Subtext, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("2. Use of Application", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("AutoDock is provided 'as is' for personal use. You may not reverse-engineer, distribute, or misuse the software in a way that disrupts device stability.", color = Subtext, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("3. Limitation of Liability", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("The developer, Kilian Ochieng, shall not be held liable for any damages or data loss resulting from the automated actions or usage of the application.", color = Subtext, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("< BACK", color = GlowBlue)
            }
        }
    }
}
