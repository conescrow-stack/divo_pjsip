package com.Fanuel.divo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fanuel.divo.data.model.CallHistory
import com.Fanuel.divo.data.model.Contact
import com.Fanuel.divo.data.model.CallStatus
import com.Fanuel.divo.ui.screens.*
import com.Fanuel.divo.ui.theme.DivoTheme
import com.Fanuel.divo.ui.viewmodel.MainViewModel
import com.Fanuel.divo.ui.viewmodel.CallHistoryViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate started")
        
        try {
            setContent {
                Log.d(TAG, "Setting content...")
                DivoTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Log.d(TAG, "Creating DivoApp...")
                        DivoApp()
                    }
                }
            }
            Log.d(TAG, "MainActivity onCreate completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "MainActivity onCreate failed", e)
            e.printStackTrace()
            throw e
        }
    }
}

@Composable
fun DivoApp(
    mainViewModel: MainViewModel = viewModel(),
    callHistoryViewModel: CallHistoryViewModel = viewModel()
) {
    Log.d("MainActivity", "DivoApp composable started")
    
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
    Log.d("MainActivity", "isLoggedIn state: $isLoggedIn")
    
    // Navigation state
    var currentRoute by remember { mutableStateOf("contacts") }
    var isInCall by remember { mutableStateOf(false) }
    
    // Call state
    var callState by remember { 
        mutableStateOf(
            com.Fanuel.divo.data.model.CallState()
        ) 
    }
    
    // Call start time for duration calculation
    var callStartTime by remember { mutableStateOf(0L) }
    
    // Show login screen first
    if (!isLoggedIn) {
        Log.d("MainActivity", "Showing LoginScreen")
        LoginScreen(
            onLoginSuccess = {
                Log.d("MainActivity", "Login successful, setting logged in")
                mainViewModel.setLoggedIn(true)
            }
        )
        return
    }
    
    Log.d("MainActivity", "User is logged in, showing main app")
    
    // Show calling screen
    if (isInCall) {
        Log.d("MainActivity", "Showing CallingScreen")
        CallingScreen(
            callState = callState,
            onNavigate = { navItem ->
                when (navItem) {
                    com.Fanuel.divo.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                    com.Fanuel.divo.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                    com.Fanuel.divo.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                    com.Fanuel.divo.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                }
            },
            onHangUp = {
                // Record call history when call ends
                val callDuration = if (callStartTime > 0) {
                    (System.currentTimeMillis() - callStartTime) / 1000
                } else {
                    0L
                }
                
                val callHistory = CallHistory(
                    id = System.currentTimeMillis(),
                    contactName = callState.contactName,
                    phoneNumber = callState.phoneNumber,
                    callDate = Date(),
                    duration = callDuration,
                    isOutgoing = true
                )
                
                callHistoryViewModel.addCallHistory(callHistory)
                
                isInCall = false
                callState = com.Fanuel.divo.data.model.CallState()
                callStartTime = 0L
            },
            onToggleSpeaker = {
                callState = callState.copy(isSpeakerOn = !callState.isSpeakerOn)
            },
            onToggleMute = {
                callState = callState.copy(isMuted = !callState.isMuted)
            },
            onSettingsClick = {
                currentRoute = "settings"
            },
            onLogoutClick = {
                mainViewModel.logout()
            }
        )
        return
    }
    
    // Show main app screens
    when (currentRoute) {
        "contacts" -> {
            ContactsScreen(
                onNavigate = { navItem ->
                    when (navItem) {
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onCallContact = { contact ->
                    callStartTime = System.currentTimeMillis()
                    callState = com.Fanuel.divo.data.model.CallState(
                        isInCall = true,
                        phoneNumber = contact.phoneNumber,
                        contactName = contact.displayName,
                        callStatus = CallStatus.CONNECTED // Skip progression for now
                    )
                    isInCall = true
                },
                onSettingsClick = {
                    currentRoute = "settings"
                },
                onLogoutClick = {
                    mainViewModel.logout()
                }
            )
        }
        "dialer" -> {
            DialpadScreen(
                onNavigate = { navItem ->
                    when (navItem) {
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onCallNumber = { phoneNumber ->
                    callStartTime = System.currentTimeMillis()
                    callState = com.Fanuel.divo.data.model.CallState(
                        isInCall = true,
                        phoneNumber = phoneNumber,
                        callStatus = CallStatus.CONNECTED // Skip progression for now
                    )
                    isInCall = true
                },
                onSettingsClick = {
                    currentRoute = "settings"
                },
                onLogoutClick = {
                    mainViewModel.logout()
                }
            )
        }
        "call_history" -> {
            CallHistoryScreen(
                onNavigate = { navItem ->
                    when (navItem) {
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onCallHistory = { callHistory ->
                    callStartTime = System.currentTimeMillis()
                    callState = com.Fanuel.divo.data.model.CallState(
                        isInCall = true,
                        phoneNumber = callHistory.phoneNumber,
                        contactName = callHistory.contactName,
                        callStatus = CallStatus.CONNECTED // Skip progression for now
                    )
                    isInCall = true
                },
                onSettingsClick = {
                    currentRoute = "settings"
                },
                onLogoutClick = {
                    mainViewModel.logout()
                }
            )
        }
        "settings" -> {
            SettingsScreen(
                onNavigate = { navItem ->
                    when (navItem) {
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.divo.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onSettingsClick = {
                    // Already on settings screen
                },
                onLogoutClick = {
                    mainViewModel.logout()
                }
            )
        }
    }
}
