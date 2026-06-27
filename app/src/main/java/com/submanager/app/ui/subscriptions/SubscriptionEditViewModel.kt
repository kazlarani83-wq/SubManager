package com.submanager.app.ui.subscriptions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.core.util.DateUtils
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.local.entity.ProductEntity
import com.submanager.app.data.local.entity.SubscriptionEntity
import com.submanager.app.data.repository.CustomerRepository
import com.submanager.app.data.repository.ProductRepository
import com.submanager.app.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionEditViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    customerRepository: CustomerRepository,
    productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Long = savedStateHandle.get<Long>("id") ?: -1L
    private val presetCustomerId: Long = savedStateHandle.get<Long>("customerId") ?: -1L
    val isNew: Boolean get() = id <= 0L

    val customers: StateFlow<List<CustomerEntity>> = customerRepository.observeActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val products: StateFlow<List<ProductEntity>> = productRepository.observeActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _state = MutableStateFlow(
        SubscriptionEntity(
            customerId = if (presetCustomerId > 0) presetCustomerId else 0,
            productName = "",
            purchaseDate = System.currentTimeMillis(),
            activationDate = System.currentTimeMillis(),
            expiryDate = DateUtils.plusDays(System.currentTimeMillis(), 30),
            validityDays = 30
        )
    )
    val state = _state.asStateFlow()

    init {
        if (!isNew) viewModelScope.launch {
            repository.getById(id)?.let { _state.value = it }
        }
    }

    fun update(transform: (SubscriptionEntity) -> SubscriptionEntity) { _state.value = transform(_state.value) }

    /** Recompute expiry from activation + validity days. */
    fun applyValidity(days: Int) = update {
        val base = it.activationDate ?: System.currentTimeMillis()
        it.copy(validityDays = days, expiryDate = DateUtils.plusDays(base, days))
    }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val s = _state.value
        if (s.productName.isNotBlank() && s.customerId > 0) {
            repository.save(s)
            onDone()
        }
    }
}
