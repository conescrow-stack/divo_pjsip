package com.Fanuel.zobi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fanuel.zobi.data.model.CallHistory
import com.Fanuel.zobi.data.model.Contact
import com.Fanuel.zobi.data.model.CallStatus
import com.Fanuel.zobi.ui.screens.*
import com.Fanuel.zobi.ui.theme.DivoTheme
import com.Fanuel.zobi.ui.viewmodel.MainViewModel
import com.Fanuel.zobi.ui.viewmodel.CallHistoryViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DivoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DivoApp()
                }
            }
        }
    }
}

@Composable
fun DivoApp(
    mainViewModel: MainViewModel = viewModel(),
    callHistoryViewModel: CallHistoryViewModel = viewModel()
) {
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
    
    // Navigation state
    var currentRoute by remember { mutableStateOf("contacts") }
    var isInCall by remember { mutableStateOf(false) }
    
    // Call state
    var callState by remember { 
        mutableStateOf(
            com.Fanuel.zobi.data.model.CallState()
        ) 
    }
    
    // Call start time for duration calculation
    var callStartTime by remember { mutableStateOf(0L) }
    
    // Show login screen first
    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                mainViewModel.setLoggedIn(true)
            }
        )
        return
    }
    
    // Show calling screen
    if (isInCall) {
        CallingScreen(
            callState = callState,
            onNavigate = { navItem ->
                when (navItem) {
                    com.Fanuel.zobi.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                    com.Fanuel.zobi.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                    com.Fanuel.zobi.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                    com.Fanuel.zobi.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
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
                callState = com.Fanuel.zobi.data.model.CallState()
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
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onCallContact = { contact ->
                    callStartTime = System.currentTimeMillis()
                    callState = com.Fanuel.zobi.data.model.CallState(
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
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onCallNumber = { phoneNumber ->
                    callStartTime = System.currentTimeMillis()
                    callState = com.Fanuel.zobi.data.model.CallState(
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
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
                    }
                },
                onCallHistory = { callHistory ->
                    callStartTime = System.currentTimeMillis()
                    callState = com.Fanuel.zobi.data.model.CallState(
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
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.CONTACTS -> currentRoute = "contacts"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.DIALPAD -> currentRoute = "dialer"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.HISTORY -> currentRoute = "call_history"
                        com.Fanuel.zobi.ui.components.DivoBottomNavItem.SETTINGS -> currentRoute = "settings"
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