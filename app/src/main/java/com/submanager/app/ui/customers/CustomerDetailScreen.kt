package com.submanager.app.ui.customers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.core.util.DateUtils
import com.submanager.app.core.util.Money
import com.submanager.app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    navController: NavController,
    viewModel: CustomerDetailViewModel = hiltViewModel()
) {
    val customer by viewModel.customer.collectAsState()
    val subs by viewModel.subscriptions.collectAsState()
    val totalSpend = subs.sumOf { it.sellingPrice }
    val due = subs.sumOf { it.dueAmount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.customerEdit(viewModel.id)) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Subscription") },
                icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                onClick = { navController.navigate(Routes.subscriptionEdit(customerId = viewModel.id)) }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        customer?.phone?.let { Text("Phone: $it") }
                        customer?.whatsapp?.let { Text("WhatsApp: $it") }
                        customer?.email?.let { Text("Email: $it") }
                        customer?.address?.let { Text("Address: $it") }
                        Spacer(Modifier.height(8.dp))
                        Text("Total Spend: ${Money.format(totalSpend)}", style = MaterialTheme.typography.titleMedium)
                        if (due > 0) Text("Due: ${Money.format(due)}", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Subscriptions (${subs.size})", style = MaterialTheme.typography.titleMedium)
            }
            items(subs, key = { it.id }) { s ->
                ListItem(
                    headlineContent = { Text(s.productName) },
                    supportingContent = { Text("Expires: ${DateUtils.format(s.expiryDate)} • ${DateUtils.remainingDays(s.expiryDate)}d left") },
                    trailingContent = { Text(Money.format(s.sellingPrice)) },
                    modifier = Modifier.clickable { navController.navigate(Routes.subscriptionDetail(s.id)) }
                )
                HorizontalDivider()
            }
        }
    }
}
