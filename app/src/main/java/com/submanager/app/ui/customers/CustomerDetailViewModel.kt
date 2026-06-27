package com.submanager.app.ui.customers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.local.entity.SubscriptionEntity
import com.submanager.app.data.repository.CustomerRepository
import com.submanager.app.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    customerRepository: CustomerRepository,
    subscriptionRepository: SubscriptionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val id: Long = savedStateHandle.get<Long>("id") ?: -1L

    val customer: StateFlow<CustomerEntity?> = customerRepository.observeById(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val subscriptions: StateFlow<List<SubscriptionEntity>> =
        subscriptionRepository.observeForCustomer(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
