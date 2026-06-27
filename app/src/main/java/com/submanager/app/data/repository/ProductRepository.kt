package com.submanager.app.data.repository

import com.submanager.app.data.local.dao.ProductDao
import com.submanager.app.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val dao: ProductDao
) {
    fun observeActive(): Flow<List<ProductEntity>> = dao.observeActive()
    fun observeById(id: Long): Flow<ProductEntity?> = dao.observeById(id)
    fun count(): Flow<Int> = dao.countActive()

    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun save(product: ProductEntity): Long =
        dao.upsert(product.copy(updatedAt = System.currentTimeMillis()))
    suspend fun moveToTrash(id: Long) = dao.moveToTrash(id)
    suspend fun restore(id: Long) = dao.restore(id)
}
