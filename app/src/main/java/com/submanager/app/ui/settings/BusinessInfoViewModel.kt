package com.submanager.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submanager.app.data.prefs.BusinessInfo
import com.submanager.app.data.prefs.BusinessPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessInfoViewModel @Inject constructor(
    private val prefs: BusinessPrefs
) : ViewModel() {

    private val _state = MutableStateFlow(BusinessInfo())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch { _state.value = prefs.get() }
    }

    fun update(transform: (BusinessInfo) -> BusinessInfo) { _state.value = transform(_state.value) }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        prefs.save(_state.value)
        onDone()
    }
}
