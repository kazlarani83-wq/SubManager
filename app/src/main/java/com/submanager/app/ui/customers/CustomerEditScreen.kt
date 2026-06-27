package com.submanager.app.ui.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerEditScreen(
    navController: NavController,
    viewModel: CustomerEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isNew) "New Customer" else "Edit Customer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.save { navController.popBackStack() } }) { Text("Save") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            field("Name *", state.name) { v -> viewModel.update { it.copy(name = v) } }
            field("Phone", state.phone ?: "") { v -> viewModel.update { it.copy(phone = v) } }
            field("WhatsApp", state.whatsapp ?: "") { v -> viewModel.update { it.copy(whatsapp = v) } }
            field("Telegram", state.telegram ?: "") { v -> viewModel.update { it.copy(telegram = v) } }
            field("Email", state.email ?: "") { v -> viewModel.update { it.copy(email = v) } }
            field("Address", state.address ?: "") { v -> viewModel.update { it.copy(address = v) } }
            field("Facebook", state.facebook ?: "") { v -> viewModel.update { it.copy(facebook = v) } }
            field("Notes", state.notes ?: "", singleLine = false) { v -> viewModel.update { it.copy(notes = v) } }
        }
    }
}

@Composable
private fun field(label: String, value: String, singleLine: Boolean = true, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth()
    )
}
