package com.submanager.app.ui.customers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerEditViewModel @Inject constructor(
    private val repository: CustomerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Long = savedStateHandle.get<Long>("id") ?: -1L
    val isNew: Boolean get() = id <= 0L

    private val _state = MutableStateFlow(CustomerEntity(name = ""))
    val state = _state.asStateFlow()

    init {
        if (!isNew) viewModelScope.launch {
            repository.getById(id)?.let { _state.value = it }
        }
    }

    fun update(transform: (CustomerEntity) -> CustomerEntity) { _state.value = transform(_state.value) }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        if (_state.value.name.isNotBlank()) {
            repository.save(_state.value)
            onDone()
        }
    }
}
