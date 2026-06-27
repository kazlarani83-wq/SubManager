package com.submanager.app.data.local.dao

import androidx.room.*
import com.submanager.app.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE isTrashed = 0 AND status != 'ARCHIVED' ORDER BY expiryDate ASC")
    fun observeActive(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE customerId = :customerId AND isTrashed = 0 ORDER BY expiryDate ASC")
    fun observeForCustomer(customerId: Long): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun observeById(id: Long): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): SubscriptionEntity?

    @Query("SELECT * FROM subscriptions WHERE isTrashed = 0")
    fun observeAllForStats(): Flow<List<SubscriptionEntity>>

    @Query("""SELECT * FROM subscriptions WHERE isTrashed = 0 AND (
        productName LIKE '%' || :q || '%' OR loginEmail LIKE '%' || :q || '%' OR
        sellerName LIKE '%' || :q || '%' OR notes LIKE '%' || :q || '%')
        ORDER BY expiryDate ASC""")
    fun search(q: String): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(sub: SubscriptionEntity): Long

    @Update suspend fun update(sub: SubscriptionEntity)

    @Query("UPDATE subscriptions SET isTrashed = 1, trashedAt = :ts WHERE id = :id")
    suspend fun moveToTrash(id: Long, ts: Long = System.currentTimeMillis())

    @Query("UPDATE subscriptions SET isTrashed = 0, trashedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)
}
