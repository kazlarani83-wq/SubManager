package com.submanager.app.data.repository

import com.submanager.app.core.util.DateUtils
import com.submanager.app.data.local.dao.RenewalDao
import com.submanager.app.data.local.dao.SubscriptionDao
import com.submanager.app.data.local.entity.RenewalEntity
import com.submanager.app.domain.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenewalRepository @Inject constructor(
    private val renewalDao: RenewalDao,
    private val subscriptionDao: SubscriptionDao
) {
    fun observeAll(): Flow<List<RenewalEntity>> = renewalDao.observeAll()
    fun observeForSubscription(subId: Long): Flow<List<RenewalEntity>> =
        renewalDao.observeForSubscription(subId)

    /**
     * One-click renewal: extends expiry by [validityDays], records a renewal row,
     * and updates the subscription status back to ACTIVE.
     */
    suspend fun renew(subscriptionId: Long, validityDays: Int, amount: Double, cost: Double, note: String? = null) {
        val sub = subscriptionDao.getById(subscriptionId) ?: return
        val base = maxOf(sub.expiryDate ?: System.currentTimeMillis(), System.currentTimeMillis())
        val newExpiry = DateUtils.plusDays(base, validityDays)
        renewalDao.insert(
            RenewalEntity(
                subscriptionId = subscriptionId,
                customerId = sub.customerId,
                previousExpiry = sub.expiryDate,
                newExpiry = newExpiry,
                validityDays = validityDays,
                amount = amount,
                cost = cost,
                note = note
            )
        )
        subscriptionDao.update(
            sub.copy(
                expiryDate = newExpiry,
                validityDays = validityDays,
                status = SubscriptionStatus.ACTIVE,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
