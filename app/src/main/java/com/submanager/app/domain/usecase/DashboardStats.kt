package com.submanager.app.domain.usecase

data class DashboardStats(
    val totalCustomers: Int = 0,
    val totalProducts: Int = 0,
    val totalAccounts: Int = 0,
    val activeAccounts: Int = 0,
    val expiredAccounts: Int = 0,
    val expiringToday: Int = 0,
    val expiringIn3Days: Int = 0,
    val expiringIn7Days: Int = 0,
    val expiringIn15Days: Int = 0,
    val expiringIn30Days: Int = 0,
    val warrantyAccounts: Int = 0,
    val nonWarrantyAccounts: Int = 0,
    val pendingRenewals: Int = 0,
    val dueAmount: Double = 0.0,
    val totalSales: Double = 0.0,
    val totalProfit: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val bestSellingProduct: String = "-",
    val topCustomerId: Long? = null,
    val revenueByMonth: List<Pair<String, Double>> = emptyList()
)
