package com.submanager.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.submanager.app.ui.navigation.Routes

/**
 * Settings hub. Each row is an extension point for a future module described in
 * the original spec (business info, invoice theme, currency, backups, etc.).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val items = listOf(
        "Business Information" to Routes.BUSINESS_INFO,
        "Invoice Theme & Currency" to Routes.SETTINGS,
        "Notification Reminders" to Routes.SETTINGS,
        "Backup & Restore" to Routes.SETTINGS,
        "Custom Fields & Categories" to Routes.SETTINGS,
        "Security (PIN / Biometric Lock)" to Routes.SETTINGS,
        "Trash Bin" to Routes.TRASH,
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(items) { (label, route) ->
                ListItem(
                    headlineContent = { Text(label) },
                    modifier = Modifier.clickable { navController.navigate(route) }
                )
                HorizontalDivider()
            }
        }
    }
}
