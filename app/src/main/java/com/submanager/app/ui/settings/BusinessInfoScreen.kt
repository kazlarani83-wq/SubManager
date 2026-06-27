package com.submanager.app.ui.settings

import androidx.compose.foundation.layout.*
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
fun BusinessInfoScreen(
    navController: NavController,
    viewModel: BusinessInfoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Information") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { TextButton(onClick = { viewModel.save { navController.popBackStack() } }) { Text("Save") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Used on generated invoices.", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(state.name, { v -> viewModel.update { it.copy(name = v) } }, label = { Text("Business Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.address, { v -> viewModel.update { it.copy(address = v) } }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.phone, { v -> viewModel.update { it.copy(phone = v) } }, label = { Text("Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(state.currency, { v -> viewModel.update { it.copy(currency = v) } }, label = { Text("Currency Symbol") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        }
    }
}
