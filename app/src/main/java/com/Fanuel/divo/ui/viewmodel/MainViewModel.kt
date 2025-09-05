package com.Fanuel.divo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Fanuel.divo.data.repository.SipConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sipConfigRepository = SipConfigRepository(application)
    
    // Always start with logged out state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    fun logout() {
        viewModelScope.launch {
            try {
                sipConfigRepository.clearSipConfig()
            } catch (e: Exception) {
                // Ignore errors when clearing config
            }
            _isLoggedIn.value = false
        }
    }
    
    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }
}
