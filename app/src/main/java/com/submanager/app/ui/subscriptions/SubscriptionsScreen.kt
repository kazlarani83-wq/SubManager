package com.submanager.app.ui.subscriptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.core.util.DateUtils
import com.submanager.app.core.util.Money
import com.submanager.app.ui.components.EmptyState
import com.submanager.app.ui.navigation.Routes
import com.submanager.app.ui.theme.DangerRed
import com.submanager.app.ui.theme.SuccessGreen
import com.submanager.app.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubscriptionsScreen(
    navController: NavController,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subs by viewModel.subscriptions.collectAsState()
    val query by viewModel.query.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Subscriptions") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.subscriptionEdit()) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add subscription")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search product, email, seller") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            )
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubFilter.entries.forEach { f ->
                    FilterChip(
                        selected = filter == f,
                        onClick = { viewModel.onFilterChange(f) },
                        label = { Text(f.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            if (subs.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Subscriptions,
                    title = "No subscriptions",
                    subtitle = "Tap + to record a sold account."
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subs, key = { it.id }) { s ->
                        val days = DateUtils.remainingDays(s.expiryDate)
                        val color = when {
                            DateUtils.isExpired(s.expiryDate) -> DangerRed
                            days <= 7 -> WarningOrange
                            else -> SuccessGreen
                        }
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                navController.navigate(Routes.subscriptionDetail(s.id))
                            },
                            shape = MaterialTheme.shapes.large
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(s.productName, style = MaterialTheme.typography.titleMedium)
                                Text(s.loginEmail ?: "-", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Exp: ${DateUtils.format(s.expiryDate)}", color = color)
                                    Text(Money.format(s.sellingPrice))
                                }
                                Text(
                                    if (DateUtils.isExpired(s.expiryDate)) "Expired" else "$days days left",
                                    color = color,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
