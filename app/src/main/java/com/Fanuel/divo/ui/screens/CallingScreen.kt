package com.Fanuel.divo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Fanuel.divo.R
import com.Fanuel.divo.data.model.CallState
import com.Fanuel.divo.ui.components.DivoBottomNavigation
import com.Fanuel.divo.ui.components.DivoHeader
import com.Fanuel.divo.ui.components.DivoBottomNavItem

@Composable
fun CallingScreen(
    callState: CallState,
    onNavigate: (DivoBottomNavItem) -> Unit,
    onHangUp: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleMute: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        DivoHeader(
            onSettingsClick = onSettingsClick,
            onLogoutClick = onLogoutClick,
            logoSize = 202.5.dp // Half size for non-login screens
        )
        
        // Call Information
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Contact Name
            Text(
                text = callState.contactName ?: callState.phoneNumber,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Phone Number
            if (callState.contactName != null) {
                Text(
                    text = callState.phoneNumber,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Call Duration
            if (callState.callStatus == com.Fanuel.divo.data.model.CallStatus.CONNECTED) {
                val minutes = callState.duration / 60
                val seconds = callState.duration % 60
                val durationText = String.format("%02d:%02d", minutes, seconds)
                
                Text(
                    text = durationText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
            
            // Call Status
            Text(
                text = when (callState.callStatus) {
                    com.Fanuel.divo.data.model.CallStatus.DIALING -> "Dialing..."
                    com.Fanuel.divo.data.model.CallStatus.CONNECTING -> "Connecting..."
                    com.Fanuel.divo.data.model.CallStatus.CONNECTED -> "Connected"
                    com.Fanuel.divo.data.model.CallStatus.DISCONNECTED -> "Call ended"
                    com.Fanuel.divo.data.model.CallStatus.FAILED -> "Call failed"
                    else -> ""
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Call Controls
            if (callState.callStatus == com.Fanuel.divo.data.model.CallStatus.CONNECTED) {
                // Show mic controls when call is connected
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    FloatingActionButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        containerColor = if (callState.isMuted) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (callState.isMuted) Color.White 
                                     else MaterialTheme.colorScheme.onSurface
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(
                                id = if (callState.isMuted) R.drawable.ic_mic_off else R.drawable.ic_mic
                            ),
                            contentDescription = if (callState.isMuted) "Unmute" else "Mute",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Speaker Button
                    FloatingActionButton(
                        onClick = onToggleSpeaker,
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        containerColor = if (callState.isSpeakerOn) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (callState.isSpeakerOn) Color.White 
                                     else MaterialTheme.colorScheme.onSurface
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(
                                id = if (callState.isSpeakerOn) R.drawable.ic_speaker else R.drawable.ic_speaker_off
                            ),
                            contentDescription = if (callState.isSpeakerOn) "Speaker On" else "Speaker Off",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            // Hang Up Button (always visible)
            Spacer(modifier = Modifier.height(32.dp))
            FloatingActionButton(
                onClick = onHangUp,
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Hang up",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        // Bottom Navigation
        DivoBottomNavigation(
            currentRoute = "calling",
            onNavigate = onNavigate
        )
    }
}
