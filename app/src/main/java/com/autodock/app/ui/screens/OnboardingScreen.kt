package com.autodock.app.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autodock.app.ui.theme.CyberBlue
import com.autodock.app.ui.theme.DeepGraphite

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    var showDisclosure by remember { mutableStateOf(true) }

    if (showDisclosure) {
        AlertDialog(
            onDismissRequest = { /* Require explicit accept */ },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "Privacy & Data Disclosure",
                    color = CyberBlue,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            },
            text = {
                Text(
                    text = "AutoDock uses Accessibility Services to detect edge-swipe gestures and overlay floating docks securely.\n\n" +
                           "It also uses App Usage Statistics to power offline AI predictions.\n\n" +
                           "We prioritize your privacy: NO screen content, typing data, or usage metrics are ever collected, transmitted, or sent off your device. Everything runs completely offline.",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            },
            confirmButton = {
                TextButton(onClick = { showDisclosure = false }) {
                    Text("I UNDERSTAND & AGREE", color = CyberBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGraphite)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "[SYSTEM.INIT]",
            style = MaterialTheme.typography.headlineLarge,
            color = CyberBlue
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "AutoDock requires deep system integration to provide AI predictions and global gestures.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("1. ENABLE GESTURES (ACCESSIBILITY)", color = CyberBlue, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("2. ENABLE PREDICTIONS (USAGE STATS)", color = CyberBlue, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("PROCEED TO DASHBOARD", color = DeepGraphite, fontWeight = FontWeight.Black, fontSize = 16.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }
    }
}
