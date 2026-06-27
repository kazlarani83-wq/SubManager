package com.submanager.app.ui.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.core.util.DateUtils
import com.submanager.app.core.util.Money
import com.submanager.app.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubscriptionDetailScreen(
    navController: NavController,
    viewModel: SubscriptionDetailViewModel = hiltViewModel()
) {
    val sub by viewModel.subscription.collectAsState()
    val history by viewModel.renewalHistory.collectAsState()
    var showRenew by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sub?.productName ?: "Subscription") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.subscriptionEdit(viewModel.id)) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Renew") },
                icon = { Icon(Icons.Filled.Autorenew, contentDescription = null) },
                onClick = { showRenew = true }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            item {
                sub?.let { s ->
                    Card(shape = MaterialTheme.shapes.large) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            row("Login Email", s.loginEmail ?: "-")
                            row("Password", s.loginPasswordEnc ?: "-")
                            row("Recovery Email", s.recoveryEmail ?: "-")
                            row("Purchase", DateUtils.format(s.purchaseDate))
                            row("Expiry", DateUtils.format(s.expiryDate))
                            row("Remaining", "${DateUtils.remainingDays(s.expiryDate)} days")
                            row("Selling Price", Money.format(s.sellingPrice))
                            row("Profit", Money.format(s.profit))
                            row("Payment", s.paymentStatus.name)
                            if (s.dueAmount > 0) row("Due", Money.format(s.dueAmount))
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Share & Invoice", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { viewModel.shareInvoice(context, viaWhatsApp = false) },
                            label = { Text("Invoice PDF") },
                            leadingIcon = { Icon(Icons.Filled.PictureAsPdf, contentDescription = null) }
                        )
                        AssistChip(
                            onClick = { viewModel.shareInvoice(context, viaWhatsApp = true) },
                            label = { Text("Invoice → WhatsApp") },
                            leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) }
                        )
                        AssistChip(
                            onClick = { viewModel.sendLoginViaWhatsApp(context) },
                            label = { Text("Send Login → WhatsApp") },
                            leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Renewal History (${history.size})", style = MaterialTheme.typography.titleMedium)
                }
            }
            items(history, key = { it.id }) { r ->
                ListItem(
                    headlineContent = { Text("+${r.validityDays ?: 0} days → ${DateUtils.format(r.newExpiry)}") },
                    supportingContent = { Text("On ${DateUtils.format(r.renewedAt)} • ${Money.format(r.amount)}") }
                )
                HorizontalDivider()
            }
        }
    }

    if (showRenew) {
        RenewDialog(
            onConfirm = { days, amount, cost ->
                viewModel.renew(days, amount, cost); showRenew = false
            },
            onDismiss = { showRenew = false }
        )
    }
}

@Composable
private fun row(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun RenewDialog(onConfirm: (Int, Double, Double) -> Unit, onDismiss: () -> Unit) {
    var days by remember { mutableStateOf("30") }
    var amount by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renew Subscription") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(days, { days = it }, label = { Text("Validity (days)") }, singleLine = true)
                OutlinedTextField(amount, { amount = it }, label = { Text("Amount charged") }, singleLine = true)
                OutlinedTextField(cost, { cost = it }, label = { Text("Cost") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(days.toIntOrNull() ?: 30, amount.toDoubleOrNull() ?: 0.0, cost.toDoubleOrNull() ?: 0.0)
            }) { Text("Renew") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
