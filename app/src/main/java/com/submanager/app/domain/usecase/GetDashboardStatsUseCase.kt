package com.submanager.app.domain.usecase

import com.submanager.app.core.util.DateUtils
import com.submanager.app.data.repository.CustomerRepository
import com.submanager.app.data.repository.ProductRepository
import com.submanager.app.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<DashboardStats> = combine(
        subscriptionRepository.observeAllForStats(),
        customerRepository.count(),
        productRepository.count()
    ) { subs, customerCount, productCount ->
        val now = System.currentTimeMillis()
        val active = subs.filter { !DateUtils.isExpired(it.expiryDate) }
        val expired = subs.filter { DateUtils.isExpired(it.expiryDate) }

        fun expiringWithin(days: Int) = subs.count {
            val r = DateUtils.remainingDays(it.expiryDate)
            r in 0..days.toLong()
        }

        val monthFmt = SimpleDateFormat("MMM", Locale.getDefault())
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        val monthlyRevenue = subs.filter {
            it.purchaseDate?.let { d ->
                val c = Calendar.getInstance().apply { timeInMillis = d }
                c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear
            } ?: false
        }.sumOf { it.sellingPrice }

        val revenueByMonth = (5 downTo 0).map { offset ->
            val c = Calendar.getInstance().apply { add(Calendar.MONTH, -offset) }
            val m = c.get(Calendar.MONTH); val y = c.get(Calendar.YEAR)
            val total = subs.filter {
                it.purchaseDate?.let { d ->
                    val cc = Calendar.getInstance().apply { timeInMillis = d }
                    cc.get(Calendar.MONTH) == m && cc.get(Calendar.YEAR) == y
                } ?: false
            }.sumOf { it.sellingPrice }
            monthFmt.format(c.time) to total
        }

        val bestSelling = subs.groupBy { it.productName }
            .maxByOrNull { it.value.size }?.key ?: "-"

        val topCustomer = subs.groupBy { it.customerId }
            .maxByOrNull { entry -> entry.value.sumOf { it.sellingPrice } }?.key

        DashboardStats(
            totalCustomers = customerCount,
            totalProducts = productCount,
            totalAccounts = subs.size,
            activeAccounts = active.size,
            expiredAccounts = expired.size,
            expiringToday = subs.count { DateUtils.remainingDays(it.expiryDate) == 0L && !DateUtils.isExpired(it.expiryDate) },
            expiringIn3Days = expiringWithin(3),
            expiringIn7Days = expiringWithin(7),
            expiringIn15Days = expiringWithin(15),
            expiringIn30Days = expiringWithin(30),
            warrantyAccounts = subs.count { it.hasWarranty },
            nonWarrantyAccounts = subs.count { !it.hasWarranty },
            pendingRenewals = expired.size + expiringWithin(7),
            dueAmount = subs.sumOf { it.dueAmount },
            totalSales = subs.sumOf { it.sellingPrice },
            totalProfit = subs.sumOf { it.profit },
            monthlyRevenue = monthlyRevenue,
            bestSellingProduct = bestSelling,
            topCustomerId = topCustomer,
            revenueByMonth = revenueByMonth
        )
    }
}
