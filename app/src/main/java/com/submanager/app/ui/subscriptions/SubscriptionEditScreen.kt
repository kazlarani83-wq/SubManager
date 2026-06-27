package com.submanager.app.ui.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionEditScreen(
    navController: NavController,
    viewModel: SubscriptionEditViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    var customerMenu by remember { mutableStateOf(false) }
    var productMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isNew) "New Subscription" else "Edit Subscription") },
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
            // Customer picker
            ExposedDropdownMenuBox(expanded = customerMenu, onExpandedChange = { customerMenu = it }) {
                OutlinedTextField(
                    value = customers.firstOrNull { it.id == state.customerId }?.name ?: "Select customer *",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Customer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = customerMenu, onDismissRequest = { customerMenu = false }) {
                    customers.forEach { c ->
                        DropdownMenuItem(text = { Text(c.name) }, onClick = {
                            viewModel.update { it.copy(customerId = c.id) }; customerMenu = false
                        })
                    }
                }
            }

            // Product picker (free text + suggestions)
            ExposedDropdownMenuBox(expanded = productMenu, onExpandedChange = { productMenu = it }) {
                OutlinedTextField(
                    value = state.productName,
                    onValueChange = { v -> viewModel.update { it.copy(productName = v) } },
                    label = { Text("Product *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productMenu) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = productMenu, onDismissRequest = { productMenu = false }) {
                    products.forEach { p ->
                        DropdownMenuItem(text = { Text(p.name) }, onClick = {
                            viewModel.update { it.copy(productName = p.name, productId = p.id, productCategory = p.category) }
                            productMenu = false
                        })
                    }
                }
            }

            field("Login Email", state.loginEmail ?: "") { v -> viewModel.update { it.copy(loginEmail = v) } }
            OutlinedTextField(
                value = state.loginPasswordEnc ?: "",
                onValueChange = { v -> viewModel.update { it.copy(loginPasswordEnc = v) } },
                label = { Text("Login Password (encrypted at rest)") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "Hide" else "Show") } },
                modifier = Modifier.fillMaxWidth()
            )
            field("Recovery Email", state.recoveryEmail ?: "") { v -> viewModel.update { it.copy(recoveryEmail = v) } }
            field("PIN", state.pinEnc ?: "") { v -> viewModel.update { it.copy(pinEnc = v) } }

            Text("Validity", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(7, 30, 90, 180, 365).forEach { d ->
                    AssistChip(onClick = { viewModel.applyValidity(d) }, label = { Text("${d}d") })
                }
            }

            field("Cost Price", state.costPrice.toString()) { v -> viewModel.update { it.copy(costPrice = v.toDoubleOrNull() ?: 0.0) } }
            field("Selling Price", state.sellingPrice.toString()) { v -> viewModel.update { it.copy(sellingPrice = v.toDoubleOrNull() ?: 0.0) } }
            field("Amount Paid", state.amountPaid.toString()) { v -> viewModel.update { it.copy(amountPaid = v.toDoubleOrNull() ?: 0.0) } }
            field("Seller Name", state.sellerName ?: "") { v -> viewModel.update { it.copy(sellerName = v) } }
            field("Notes", state.notes ?: "", singleLine = false) { v -> viewModel.update { it.copy(notes = v) } }

            Text("Profit: ${state.sellingPrice - state.costPrice}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun field(label: String, value: String, singleLine: Boolean = true, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label) },
        singleLine = singleLine, modifier = Modifier.fillMaxWidth()
    )
}
