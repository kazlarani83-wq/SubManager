package com.submanager.app.ui.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.ui.components.ConfirmDialog
import com.submanager.app.ui.components.EmptyState
import com.submanager.app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    navController: NavController,
    viewModel: CustomersViewModel = hiltViewModel()
) {
    val customers by viewModel.customers.collectAsState()
    val query by viewModel.query.collectAsState()
    var pendingDelete by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Customers") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.customerEdit()) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add customer")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search name, phone, email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            )
            if (customers.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Group,
                    title = "No customers yet",
                    subtitle = "Tap + to add your first customer."
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    items(customers, key = { it.id }) { c ->
                        ListItem(
                            headlineContent = { Text(c.name) },
                            supportingContent = { Text(c.phone ?: c.email ?: "-") },
                            leadingContent = {
                                IconButton(onClick = { viewModel.toggleFavorite(c) }) {
                                    Icon(
                                        if (c.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Favorite"
                                    )
                                }
                            },
                            trailingContent = {
                                TextButton(onClick = { pendingDelete = c.id }) { Text("Delete") }
                            },
                            modifier = Modifier.clickable { navController.navigate(Routes.customerDetail(c.id)) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    pendingDelete?.let { id ->
        ConfirmDialog(
            title = "Move to Trash?",
            message = "This customer will be moved to Trash. You can restore within 30 days.",
            confirmLabel = "Move to Trash",
            onConfirm = { viewModel.moveToTrash(id); pendingDelete = null },
            onDismiss = { pendingDelete = null }
        )
    }
}

private fun Modifier.clickable(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
