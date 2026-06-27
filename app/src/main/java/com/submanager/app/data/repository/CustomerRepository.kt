package com.submanager.app.data.repository

import com.submanager.app.data.local.dao.CustomerDao
import com.submanager.app.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val dao: CustomerDao
) {
    fun observeActive(): Flow<List<CustomerEntity>> = dao.observeActive()
    fun observeTrashed(): Flow<List<CustomerEntity>> = dao.observeTrashed()
    fun observeById(id: Long): Flow<CustomerEntity?> = dao.observeById(id)
    fun count(): Flow<Int> = dao.countActive()
    fun search(q: String): Flow<List<CustomerEntity>> = dao.search(q)

    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun save(customer: CustomerEntity): Long =
        dao.upsert(customer.copy(updatedAt = System.currentTimeMillis()))
    suspend fun moveToTrash(id: Long) = dao.moveToTrash(id)
    suspend fun restore(id: Long) = dao.restore(id)
    suspend fun purgeOldTrash(cutoff: Long) = dao.purgeOldTrash(cutoff)
}
