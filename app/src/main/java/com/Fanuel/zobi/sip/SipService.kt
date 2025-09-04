package com.Fanuel.zobi.sip

import android.app.*
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.Fanuel.zobi.R
import com.Fanuel.zobi.data.model.AudioCodec
import com.Fanuel.zobi.data.model.CallState
import com.Fanuel.zobi.data.model.CallStatus
import com.Fanuel.zobi.data.model.SipConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class SipService : Service() {
    
    companion object {
        private const val TAG = "SipService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "sip_service_channel"
    }
    
    private val binder = SipBinder()
    private var callTimer: Job? = null
    
    private val _callState = MutableStateFlow(CallState())
    val callState: StateFlow<CallState> = _callState.asStateFlow()
    
    private val audioManager by lazy { getSystemService(AUDIO_SERVICE) as AudioManager }
    
    inner class SipBinder : Binder() {
        fun getService(): SipService = this@SipService
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        disconnectCall()
    }
    
    fun registerAccount(config: SipConfig, onResult: (Boolean, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulate registration delay
                delay(1000)
                
                withContext(Dispatchers.Main) {
                    onResult(true, null)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Registration failed", e)
                withContext(Dispatchers.Main) {
                    onResult(false, e.message)
                }
            }
        }
    }
    
    fun makeCall(phoneNumber: String, contactName: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            _callState.value = CallState(
                isInCall = true,
                phoneNumber = phoneNumber,
                contactName = contactName,
                callStatus = CallStatus.DIALING
            )
            
            // Simulate call progression
            delay(2000)
            _callState.value = _callState.value.copy(callStatus = CallStatus.CONNECTING)
            
            delay(1000)
            _callState.value = _callState.value.copy(callStatus = CallStatus.CONNECTED)
            startCallTimer()
        }
    }
    
    fun disconnectCall() {
        callTimer?.cancel()
        
        _callState.value = CallState(
            isInCall = false,
            callStatus = CallStatus.DISCONNECTED
        )
    }
    
    fun toggleSpeaker() {
        val currentState = _callState.value
        val newSpeakerState = !currentState.isSpeakerOn
        
        if (newSpeakerState) {
            audioManager.isSpeakerphoneOn = true
            audioManager.mode = AudioManager.MODE_NORMAL
        } else {
            audioManager.isSpeakerphoneOn = false
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        }
        
        _callState.value = currentState.copy(isSpeakerOn = newSpeakerState)
    }
    
    fun toggleMute() {
        val currentState = _callState.value
        val newMuteState = !currentState.isMuted
        
        _callState.value = currentState.copy(isMuted = newMuteState)
    }
    
    private fun startCallTimer() {
        callTimer = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(1000)
                val currentDuration = _callState.value.duration
                _callState.value = _callState.value.copy(duration = currentDuration + 1)
            }
        }
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "SIP Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "SIP service for handling calls"
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SIP Service")
            .setContentText("SIP service is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
