package com.submanager.app.data.repository

import com.submanager.app.core.crypto.CryptoManager
import com.submanager.app.data.local.dao.SubscriptionDao
import com.submanager.app.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository transparently encrypts/decrypts sensitive credential fields so the
 * rest of the app works with plaintext while storage stays encrypted.
 */
@Singleton
class SubscriptionRepository @Inject constructor(
    private val dao: SubscriptionDao,
    private val crypto: CryptoManager
) {
    fun observeActive(): Flow<List<SubscriptionEntity>> =
        dao.observeActive().map { list -> list.map { it.decrypted() } }

    fun observeForCustomer(customerId: Long): Flow<List<SubscriptionEntity>> =
        dao.observeForCustomer(customerId).map { list -> list.map { it.decrypted() } }

    fun observeById(id: Long): Flow<SubscriptionEntity?> =
        dao.observeById(id).map { it?.decrypted() }

    fun observeAllForStats(): Flow<List<SubscriptionEntity>> =
        dao.observeAllForStats().map { list -> list.map { it.decrypted() } }

    fun search(q: String): Flow<List<SubscriptionEntity>> =
        dao.search(q).map { list -> list.map { it.decrypted() } }

    suspend fun getById(id: Long): SubscriptionEntity? = dao.getById(id)?.decrypted()

    suspend fun save(sub: SubscriptionEntity): Long =
        dao.upsert(sub.copy(updatedAt = System.currentTimeMillis()).encrypted())

    suspend fun update(sub: SubscriptionEntity) =
        dao.update(sub.copy(updatedAt = System.currentTimeMillis()).encrypted())

    suspend fun moveToTrash(id: Long) = dao.moveToTrash(id)
    suspend fun restore(id: Long) = dao.restore(id)

    private fun SubscriptionEntity.encrypted() = copy(
        loginPasswordEnc = crypto.encrypt(loginPasswordEnc),
        pinEnc = crypto.encrypt(pinEnc),
        backupCodesEnc = crypto.encrypt(backupCodesEnc)
    )

    private fun SubscriptionEntity.decrypted() = copy(
        loginPasswordEnc = crypto.decrypt(loginPasswordEnc),
        pinEnc = crypto.decrypt(pinEnc),
        backupCodesEnc = crypto.decrypt(backupCodesEnc)
    )
}
