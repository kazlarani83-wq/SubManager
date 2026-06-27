package com.submanager.app.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.core.util.DateUtils
import com.submanager.app.data.local.entity.SubscriptionEntity
import com.submanager.app.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SubFilter { ALL, ACTIVE, EXPIRED, EXPIRING_SOON }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    val query = MutableStateFlow("")
    val filter = MutableStateFlow(SubFilter.ALL)

    private val source = query.debounce(200).flatMapLatest { q ->
        if (q.isBlank()) repository.observeActive() else repository.search(q)
    }

    val subscriptions: StateFlow<List<SubscriptionEntity>> =
        combine(source, filter) { list, f ->
            when (f) {
                SubFilter.ALL -> list
                SubFilter.ACTIVE -> list.filter { !DateUtils.isExpired(it.expiryDate) }
                SubFilter.EXPIRED -> list.filter { DateUtils.isExpired(it.expiryDate) }
                SubFilter.EXPIRING_SOON -> list.filter { DateUtils.remainingDays(it.expiryDate) in 0..7 }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(q: String) { query.value = q }
    fun onFilterChange(f: SubFilter) { filter.value = f }
    fun moveToTrash(id: Long) = viewModelScope.launch { repository.moveToTrash(id) }
}
