package com.Fanuel.divo.sip

import android.util.Log
import com.Fanuel.divo.data.model.SipConfig
import org.pjsip.pjsua2.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class PjsipAccountManager {
    
    companion object {
        private const val TAG = "PjsipAccountManager"
    }
    
    // Registration status
    private val _registrationStatus = MutableStateFlow<RegistrationStatus>(RegistrationStatus.Disconnected)
    val registrationStatus: StateFlow<RegistrationStatus> = _registrationStatus.asStateFlow()
    
    // PJSIP objects
    private var endpoint: Endpoint? = null
    private var account: MyAccount? = null
    
    // Registration status enum
    sealed class RegistrationStatus {
        object Disconnected : RegistrationStatus()
        object Connecting : RegistrationStatus()
        object Connected : RegistrationStatus()
        data class Failed(val errorCode: Int, val errorMessage: String) : RegistrationStatus()
    }
    
    // Custom Account class to handle registration callbacks
    inner class MyAccount : Account() {
        override fun onRegState(prm: OnRegStateParam) {
            val code = prm.code.swigValue()  // Get the integer value from PJSIP status code
            val reason = prm.reason
            
            Log.d(TAG, "Registration state changed: $code $reason")
            
            when (code) {
                200 -> {
                    Log.i(TAG, "✅ SIP Registration successful")
                    _registrationStatus.value = RegistrationStatus.Connected
                }
                401 -> {
                    Log.e(TAG, "❌ SIP Authentication failed - wrong credentials")
                    _registrationStatus.value = RegistrationStatus.Failed(401, "Authentication failed - check username/password")
                }
                403 -> {
                    Log.e(TAG, "❌ SIP Forbidden - account disabled")
                    _registrationStatus.value = RegistrationStatus.Failed(403, "Account disabled")
                }
                404 -> {
                    Log.e(TAG, "❌ SIP Server not found")
                    _registrationStatus.value = RegistrationStatus.Failed(404, "SIP server not found")
                }
                408 -> {
                    Log.e(TAG, "❌ SIP Request timeout - server unreachable")
                    _registrationStatus.value = RegistrationStatus.Failed(408, "Server unreachable - check IP address")
                }
                else -> {
                    Log.e(TAG, "❌ SIP Registration failed: $code $reason")
                    _registrationStatus.value = RegistrationStatus.Failed(code, "Registration failed: $reason")
                }
            }
        }
    }
    
    // Initialize PJSIP endpoint
    fun initialize(): Boolean {
        return try {
            Log.d(TAG, "Initializing PJSIP endpoint...")
            
            // Load C++ standard library first (required dependency)
            try {
                System.loadLibrary("c++_shared")
                Log.d(TAG, "✅ C++ standard library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "❌ C++ standard library not found: ${e.message}")
                _registrationStatus.value = RegistrationStatus.Failed(-1, "C++ standard library not found: ${e.message}")
                return false
            }
            
            // Load the main PJSIP library
            try {
                System.loadLibrary("pjsua2")
                Log.d(TAG, "✅ PJSIP native library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "❌ Failed to load libpjsua2.so: ${e.message}")
                Log.e(TAG, "❌ Error details: ${e.toString()}")
                _registrationStatus.value = RegistrationStatus.Failed(-1, "PJSIP library loading failed: ${e.message}")
                return false
            }
            
            // Create endpoint configuration
            val epConfig = EpConfig()
            epConfig.logConfig.level = 4  // Enable detailed logging
            epConfig.logConfig.consoleLevel = 4
            
            Log.d(TAG, "Creating PJSIP endpoint...")
            // Create and initialize endpoint
            endpoint = Endpoint()
            
            Log.d(TAG, "Calling libCreate()...")
            endpoint?.libCreate()
            
            Log.d(TAG, "Calling libInit()...")
            // Initialize endpoint with configuration
            endpoint?.libInit(epConfig)
            
            Log.d(TAG, "Calling libStart()...")
            // Start endpoint
            endpoint?.libStart()
            
            Log.i(TAG, "✅ PJSIP endpoint initialized successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize PJSIP endpoint", e)
            e.printStackTrace()
            _registrationStatus.value = RegistrationStatus.Failed(-1, "PJSIP initialization failed: ${e.message}")
            false
        }
    }
    
    // Register SIP account
    fun registerAccount(sipConfig: SipConfig): Boolean {
        return try {
            Log.d(TAG, "Registering SIP account: ${sipConfig.username}@${sipConfig.domain}")
            
            _registrationStatus.value = RegistrationStatus.Connecting
            
            // Create account configuration
            val accConfig = AccountConfig()
            
            // Set account ID (username@domain)
            accConfig.idUri = "sip:${sipConfig.username}@${sipConfig.domain}"
            
            // Set registrar URI
            accConfig.regConfig.registrarUri = "sip:${sipConfig.domain}"
            
            // Set credentials
            val authCred = AuthCredInfo(
                "digest",           // auth_type
                "*",                // realm
                sipConfig.username, // username
                0,                  // data_type
                sipConfig.password  // password
            )
            accConfig.sipConfig.authCreds.add(authCred)
            
            // Create and register account
            account = MyAccount()
            account?.create(accConfig)
            account?.setRegistration(true)
            
            Log.d(TAG, "SIP account registration initiated")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to register SIP account", e)
            e.printStackTrace()
            _registrationStatus.value = RegistrationStatus.Failed(-1, "Registration failed: ${e.message}")
            false
        }
    }
    
    // Unregister and cleanup
    fun unregister() {
        try {
            Log.d(TAG, "Unregistering SIP account...")
            
            account?.setRegistration(false)
            account?.delete()
            account = null
            
            endpoint?.libDestroy()
            endpoint = null
            
            _registrationStatus.value = RegistrationStatus.Disconnected
            Log.i(TAG, "✅ SIP account unregistered successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error during unregistration", e)
        }
    }
    
    // Check if currently registered
    fun isRegistered(): Boolean {
        return _registrationStatus.value is RegistrationStatus.Connected
    }
    
    // Get current registration status
    fun getCurrentStatus(): RegistrationStatus {
        return _registrationStatus.value
    }
}
