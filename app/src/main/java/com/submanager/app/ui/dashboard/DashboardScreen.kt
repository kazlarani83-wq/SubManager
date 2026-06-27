package com.submanager.app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.submanager.app.core.util.Money
import com.submanager.app.ui.components.MiniBarChart
import com.submanager.app.ui.components.StatCard
import com.submanager.app.ui.theme.DangerRed
import com.submanager.app.ui.theme.SuccessGreen
import com.submanager.app.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    val cards = listOf(
        Triple("Total Customers", stats.totalCustomers.toString(), MaterialTheme.colorScheme.primary),
        Triple("Total Products", stats.totalProducts.toString(), MaterialTheme.colorScheme.primary),
        Triple("Total Accounts", stats.totalAccounts.toString(), MaterialTheme.colorScheme.secondary),
        Triple("Active", stats.activeAccounts.toString(), SuccessGreen),
        Triple("Expired", stats.expiredAccounts.toString(), DangerRed),
        Triple("Expiring Today", stats.expiringToday.toString(), WarningOrange),
        Triple("Expiring 3 Days", stats.expiringIn3Days.toString(), WarningOrange),
        Triple("Expiring 7 Days", stats.expiringIn7Days.toString(), WarningOrange),
        Triple("Expiring 15 Days", stats.expiringIn15Days.toString(), WarningOrange),
        Triple("Expiring 30 Days", stats.expiringIn30Days.toString(), MaterialTheme.colorScheme.tertiary),
        Triple("Warranty", stats.warrantyAccounts.toString(), MaterialTheme.colorScheme.secondary),
        Triple("Non-Warranty", stats.nonWarrantyAccounts.toString(), MaterialTheme.colorScheme.outline),
        Triple("Pending Renewals", stats.pendingRenewals.toString(), WarningOrange),
        Triple("Due Amount", Money.format(stats.dueAmount), DangerRed),
        Triple("Total Sales", Money.format(stats.totalSales), MaterialTheme.colorScheme.primary),
        Triple("Total Profit", Money.format(stats.totalProfit), SuccessGreen),
        Triple("Monthly Revenue", Money.format(stats.monthlyRevenue), MaterialTheme.colorScheme.secondary),
        Triple("Best Seller", stats.bestSellingProduct, MaterialTheme.colorScheme.tertiary),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate(com.submanager.app.ui.navigation.Routes.SETTINGS) }) {
                        Icon(androidx.compose.material.icons.Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Revenue (last 6 months)", style = MaterialTheme.typography.titleMedium)
                        MiniBarChart(data = stats.revenueByMonth)
                    }
                }
            }
            items(cards) { (label, value, color) ->
                StatCard(label = label, value = value, accent = color)
            }
        }
    }
}
