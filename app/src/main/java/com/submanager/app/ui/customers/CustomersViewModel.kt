package com.submanager.app.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.data.local.entity.CustomerEntity
import com.submanager.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    val query = MutableStateFlow("")

    val customers: StateFlow<List<CustomerEntity>> = query
        .debounce(200)
        .flatMapLatest { q ->
            if (q.isBlank()) repository.observeActive() else repository.search(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(q: String) { query.value = q }

    fun moveToTrash(id: Long) = viewModelScope.launch { repository.moveToTrash(id) }
    fun toggleFavorite(c: CustomerEntity) = viewModelScope.launch {
        repository.save(c.copy(isFavorite = !c.isFavorite))
    }
}
