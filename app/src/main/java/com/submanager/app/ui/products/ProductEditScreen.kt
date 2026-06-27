package com.submanager.app.ui.products

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
fun ProductEditScreen(
    navController: NavController,
    viewModel: ProductEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isNew) "New Product" else "Edit Product") },
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
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            f("Name *", state.name) { v -> viewModel.update { it.copy(name = v) } }
            f("Category", state.category ?: "") { v -> viewModel.update { it.copy(category = v) } }
            f("Available Accounts", state.availableAccounts.toString()) { v -> viewModel.update { it.copy(availableAccounts = v.toIntOrNull() ?: 0) } }
            f("Sold Accounts", state.soldAccounts.toString()) { v -> viewModel.update { it.copy(soldAccounts = v.toIntOrNull() ?: 0) } }
            f("Low Stock Threshold", state.lowStockThreshold.toString()) { v -> viewModel.update { it.copy(lowStockThreshold = v.toIntOrNull() ?: 3) } }
            f("Default Cost Price", state.defaultCostPrice.toString()) { v -> viewModel.update { it.copy(defaultCostPrice = v.toDoubleOrNull() ?: 0.0) } }
            f("Default Selling Price", state.defaultSellingPrice.toString()) { v -> viewModel.update { it.copy(defaultSellingPrice = v.toDoubleOrNull() ?: 0.0) } }
            f("Notes", state.notes ?: "", false) { v -> viewModel.update { it.copy(notes = v) } }
        }
    }
}

@Composable
private fun f(label: String, value: String, singleLine: Boolean = true, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, label = { Text(label) }, singleLine = singleLine, modifier = Modifier.fillMaxWidth())
}
