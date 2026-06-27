package com.submanager.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.submanager.app.ui.customers.CustomerDetailScreen
import com.submanager.app.ui.customers.CustomerEditScreen
import com.submanager.app.ui.customers.CustomersScreen
import com.submanager.app.ui.dashboard.DashboardScreen
import com.submanager.app.ui.products.ProductEditScreen
import com.submanager.app.ui.products.ProductsScreen
import com.submanager.app.ui.settings.BusinessInfoScreen
import com.submanager.app.ui.settings.SettingsScreen
import com.submanager.app.ui.subscriptions.SubscriptionDetailScreen
import com.submanager.app.ui.subscriptions.SubscriptionEditScreen
import com.submanager.app.ui.subscriptions.SubscriptionsScreen
import com.submanager.app.ui.trash.TrashScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = Routes.DASHBOARD) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.DASHBOARD) { DashboardScreen(navController) }
        composable(Routes.SUBSCRIPTIONS) { SubscriptionsScreen(navController) }
        composable(Routes.CUSTOMERS) { CustomersScreen(navController) }
        composable(Routes.PRODUCTS) { ProductsScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(Routes.BUSINESS_INFO) { BusinessInfoScreen(navController) }
        composable(Routes.TRASH) { TrashScreen(navController) }

        composable(
            Routes.CUSTOMER_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { CustomerDetailScreen(navController) }

        composable(
            Routes.CUSTOMER_EDIT,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { CustomerEditScreen(navController) }

        composable(
            Routes.SUBSCRIPTION_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { SubscriptionDetailScreen(navController) }

        composable(
            Routes.SUBSCRIPTION_EDIT,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType; defaultValue = -1L },
                navArgument("customerId") { type = NavType.LongType; defaultValue = -1L }
            )
        ) { SubscriptionEditScreen(navController) }

        composable(
            Routes.PRODUCT_EDIT,
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { ProductEditScreen(navController) }
    }
}
