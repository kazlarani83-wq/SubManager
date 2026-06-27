package com.submanager.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val DASHBOARD = "dashboard"
    const val CUSTOMERS = "customers"
    const val SUBSCRIPTIONS = "subscriptions"
    const val PRODUCTS = "products"
    const val SETTINGS = "settings"
    const val TRASH = "trash"
    const val BUSINESS_INFO = "business_info"

    const val CUSTOMER_DETAIL = "customer/{id}"
    const val CUSTOMER_EDIT = "customer_edit?id={id}"
    const val SUBSCRIPTION_DETAIL = "subscription/{id}"
    const val SUBSCRIPTION_EDIT = "subscription_edit?id={id}&customerId={customerId}"
    const val PRODUCT_EDIT = "product_edit?id={id}"

    fun customerDetail(id: Long) = "customer/$id"
    fun customerEdit(id: Long? = null) = "customer_edit?id=${id ?: -1}"
    fun subscriptionDetail(id: Long) = "subscription/$id"
    fun subscriptionEdit(id: Long? = null, customerId: Long? = null) =
        "subscription_edit?id=${id ?: -1}&customerId=${customerId ?: -1}"
    fun productEdit(id: Long? = null) = "product_edit?id=${id ?: -1}"
}

enum class TopLevelDestination(val route: String, val label: String, val icon: ImageVector) {
    DASHBOARD(Routes.DASHBOARD, "Dashboard", Icons.Outlined.Dashboard),
    SUBSCRIPTIONS(Routes.SUBSCRIPTIONS, "Subs", Icons.Outlined.Subscriptions),
    CUSTOMERS(Routes.CUSTOMERS, "Customers", Icons.Outlined.Group),
    PRODUCTS(Routes.PRODUCTS, "Products", Icons.Outlined.Inventory2),
}
