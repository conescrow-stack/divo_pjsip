package com.Fanuel.divo.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.Fanuel.divo.data.model.AudioCodec
import com.Fanuel.divo.data.model.SipConfig
import com.Fanuel.divo.data.repository.SipConfigRepository
import com.Fanuel.divo.sip.PjsipAccountManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sipConfigRepository = SipConfigRepository(application)
    private var pjsipAccountManager: PjsipAccountManager? = null
    
    init {
        Log.d("LoginViewModel", "=== LoginViewModel constructor started ===")
        try {
            Log.d("LoginViewModel", "Creating PjsipAccountManager...")
            pjsipAccountManager = PjsipAccountManager()
            Log.d("LoginViewModel", "✅ PjsipAccountManager created successfully")
        } catch (e: Exception) {
            Log.e("LoginViewModel", "❌ Failed to create PjsipAccountManager", e)
            e.printStackTrace()
        }
        Log.d("LoginViewModel", "=== LoginViewModel constructor completed ===")
    }
    
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    // Start with clean config, remember me enabled by default
    private val _sipConfig = MutableStateFlow(SipConfig(rememberMe = true))
    val sipConfig: StateFlow<SipConfig> = _sipConfig.asStateFlow()
    
    // PJSIP registration status
    val registrationStatus = pjsipAccountManager?.registrationStatus ?: MutableStateFlow(PjsipAccountManager.RegistrationStatus.Disconnected)
    
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
        Log.d("LoginViewModel", "=== LOGIN FUNCTION STARTED ===")
        
        val config = _sipConfig.value

        Log.d("LoginViewModel", "Login attempt started")

        // Validate required fields
        if (config.username.isBlank() || config.password.isBlank() || config.domain.isBlank()) {
            Log.d("LoginViewModel", "Validation failed - empty fields")
            _loginState.value = LoginState(
                isLoading = false,
                error = "Please fill in all fields"
            )
            return
        }

        Log.d("LoginViewModel", "Validation passed, setting loading state")
        _loginState.value = LoginState(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Starting PJSIP initialization...")

                // Initialize PJSIP endpoint
                val initSuccess = pjsipAccountManager?.initialize() ?: false
                if (!initSuccess) {
                    Log.e("LoginViewModel", "PJSIP initialization failed")
                    _loginState.value = LoginState(
                        isLoading = false,
                        error = "Failed to initialize PJSIP"
                    )
                    return@launch
                }

                Log.d("LoginViewModel", "PJSIP initialized successfully, attempting registration...")

                // Attempt SIP registration
                val regSuccess = pjsipAccountManager?.registerAccount(config) ?: false
                if (!regSuccess) {
                    Log.e("LoginViewModel", "SIP registration initiation failed")
                    _loginState.value = LoginState(
                        isLoading = false,
                        error = "Failed to initiate SIP registration"
                    )
                    return@launch
                }

                Log.d("LoginViewModel", "SIP registration initiated, monitoring status...")

                // Monitor registration status
                pjsipAccountManager?.registrationStatus?.collect { status ->
                    Log.d("LoginViewModel", "Registration status update: $status")
                    when (status) {
                        is PjsipAccountManager.RegistrationStatus.Connected -> {
                            // Registration successful
                            Log.i("LoginViewModel", "SIP registration successful!")
                            _loginState.value = LoginState(isLoading = false, error = null)

                            // Save configuration if remember me is checked
                            if (config.rememberMe) {
                                Log.d("LoginViewModel", "Saving SIP config to repository")
                                sipConfigRepository.saveSipConfig(config)
                            }

                            onSuccess()
                        }
                        is PjsipAccountManager.RegistrationStatus.Failed -> {
                            // Registration failed
                            Log.e("LoginViewModel", "SIP registration failed: ${status.errorMessage}")
                            _loginState.value = LoginState(
                                isLoading = false,
                                error = "SIP Registration failed: ${status.errorMessage}"
                            )
                        }
                        is PjsipAccountManager.RegistrationStatus.Connecting -> {
                            // Still connecting, keep loading
                            Log.d("LoginViewModel", "Still connecting to SIP server...")
                            _loginState.value = LoginState(isLoading = true, error = null)
                        }
                        is PjsipAccountManager.RegistrationStatus.Disconnected -> {
                            // Disconnected state
                            Log.d("LoginViewModel", "SIP connection lost")
                            _loginState.value = LoginState(
                                isLoading = false,
                                error = "SIP connection lost"
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed with exception", e)
                e.printStackTrace()
                _loginState.value = LoginState(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }
    
    fun logout() {
        pjsipAccountManager?.unregister()
        _loginState.value = LoginState()
    }
    
    override fun onCleared() {
        super.onCleared()
        pjsipAccountManager?.unregister()
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)
