package com.submanager.app.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.ui.components.EmptyState
import com.submanager.app.ui.navigation.Routes
import com.submanager.app.ui.theme.DangerRed
import com.submanager.app.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inventory") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.productEdit()) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Inventory2,
                title = "No products",
                subtitle = "Add products to track available and sold stock.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products, key = { it.id }) { p ->
                    Card(
                        Modifier.fillMaxWidth().clickable { navController.navigate(Routes.productEdit(p.id)) },
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(p.name, style = MaterialTheme.typography.titleMedium)
                            p.category?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                            Spacer(Modifier.height(6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Available: ${p.availableAccounts}")
                                Text("Sold: ${p.soldAccounts}")
                                Text("Stock: ${p.remainingStock}")
                            }
                            when {
                                p.isOutOfStock -> Text("Out of stock", color = DangerRed, style = MaterialTheme.typography.labelMedium)
                                p.isLowStock -> Text("Low stock", color = WarningOrange, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
