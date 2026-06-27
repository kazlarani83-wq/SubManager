package com.submanager.app.ui.trash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    navController: NavController,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val customers by viewModel.trashedCustomers.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trash") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (customers.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.DeleteOutline,
                title = "Trash is empty",
                subtitle = "Deleted items appear here and auto-delete after 30 days.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(customers, key = { it.id }) { c ->
                    ListItem(
                        headlineContent = { Text(c.name) },
                        supportingContent = { Text("Customer") },
                        trailingContent = { TextButton(onClick = { viewModel.restore(c.id) }) { Text("Restore") } }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
