package com.Fanuel.zobi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Fanuel.zobi.data.model.AudioCodec
import com.Fanuel.zobi.data.model.SipConfig
import com.Fanuel.zobi.data.repository.SipConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sipConfigRepository = SipConfigRepository(application)
    
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    // Start with clean config, remember me enabled by default
    private val _sipConfig = MutableStateFlow(SipConfig(rememberMe = true))
    val sipConfig: StateFlow<SipConfig> = _sipConfig.asStateFlow()
    
    fun updateUsername(username: String) {
        _sipConfig.value = _sipConfig.value.copy(username = username)
    }
    
    fun updatePassword(password: String) {
        _sipConfig.value = _sipConfig.value.copy(password = password)
    }
    
    fun updateDomain(domain: String) {
        _sipConfig.value = _sipConfig.value.copy(domain = domain)
    }
    
    fun updateAudioCodec(codec: AudioCodec) {
        _sipConfig.value = _sipConfig.value.copy(audioCodec = codec)
    }
    
    fun updateRememberMe(rememberMe: Boolean) {
        _sipConfig.value = _sipConfig.value.copy(rememberMe = rememberMe)
    }
    
    fun login(onSuccess: () -> Unit) {
        val config = _sipConfig.value
        
        // Demo login - allow login with demo credentials
        if (config.username.isBlank() && config.password.isBlank() && config.domain.isBlank()) {
            // Auto-fill demo credentials if all fields are empty
            _sipConfig.value = config.copy(
                username = "demo_user",
                password = "demo_pass",
                domain = "192.168.1.100"
            )
        }
        
        val currentConfig = _sipConfig.value
        
        if (currentConfig.username.isBlank() || currentConfig.password.isBlank() || currentConfig.domain.isBlank()) {
            _loginState.value = LoginState(
                isLoading = false,
                error = "Please fill in all fields"
            )
            return
        }
        
        _loginState.value = LoginState(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                // Save configuration if remember me is checked
                if (currentConfig.rememberMe) {
                    sipConfigRepository.saveSipConfig(currentConfig)
                }
                
                // For demo purposes, always succeed
                kotlinx.coroutines.delay(1000) // Simulate network delay
                _loginState.value = LoginState(isLoading = false, error = null)
                onSuccess()
                
            } catch (e: Exception) {
                _loginState.value = LoginState(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)
