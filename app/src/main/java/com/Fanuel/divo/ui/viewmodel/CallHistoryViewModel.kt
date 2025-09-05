package com.Fanuel.divo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Fanuel.divo.data.model.CallHistory
import com.Fanuel.divo.data.repository.CallHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CallHistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val callHistoryRepository = CallHistoryRepository(application)
    
    private val _callHistoryState = MutableStateFlow(CallHistoryState())
    val callHistoryState: StateFlow<CallHistoryState> = _callHistoryState.asStateFlow()
    
    init {
        loadCallHistory()
    }
    
    fun loadCallHistory() {
        _callHistoryState.value = _callHistoryState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val history = callHistoryRepository.getCallHistory()
                _callHistoryState.value = CallHistoryState(
                    callHistory = history,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _callHistoryState.value = CallHistoryState(
                    callHistory = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Failed to load call history"
                )
            }
        }
    }
    
    fun addCallHistory(callHistory: CallHistory) {
        viewModelScope.launch {
            try {
                callHistoryRepository.addCallHistory(callHistory)
                loadCallHistory() // Reload the list
            } catch (e: Exception) {
                _callHistoryState.value = _callHistoryState.value.copy(
                    error = e.message ?: "Failed to add call history"
                )
            }
        }
    }
    
    fun clearCallHistory() {
        viewModelScope.launch {
            try {
                callHistoryRepository.clearCallHistory()
                _callHistoryState.value = CallHistoryState(
                    callHistory = emptyList(),
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _callHistoryState.value = _callHistoryState.value.copy(
                    error = e.message ?: "Failed to clear call history"
                )
            }
        }
    }
    
    fun getCallHistoryFlow() = callHistoryRepository.getCallHistoryFlow()
}

data class CallHistoryState(
    val callHistory: List<CallHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
