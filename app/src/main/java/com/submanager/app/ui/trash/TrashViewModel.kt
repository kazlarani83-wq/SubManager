package com.submanager.app.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    val trashedCustomers: StateFlow<List<CustomerEntity>> =
        customerRepository.observeTrashed()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restore(id: Long) = viewModelScope.launch { customerRepository.restore(id) }
}
