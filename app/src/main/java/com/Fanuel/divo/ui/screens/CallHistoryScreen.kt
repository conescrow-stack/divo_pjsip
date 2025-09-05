package com.Fanuel.divo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Fanuel.divo.data.model.CallHistory
import com.Fanuel.divo.ui.components.DivoBottomNavigation
import com.Fanuel.divo.ui.components.DivoHeader
import com.Fanuel.divo.ui.components.DivoBottomNavItem
import com.Fanuel.divo.ui.viewmodel.CallHistoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

@Composable
fun CallHistoryScreen(
    onNavigate: (DivoBottomNavItem) -> Unit,
    onCallHistory: (CallHistory) -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val viewModel: CallHistoryViewModel = viewModel()
    val callHistoryState by viewModel.callHistoryState.collectAsState()
    
    // Add demo call history if empty
    LaunchedEffect(Unit) {
        if (callHistoryState.callHistory.isEmpty()) {
            addDemoCallHistory(viewModel)
        }
    }
    
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
        
        // Call History List
        if (callHistoryState.callHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "No Call History",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Your call history will appear here after making calls",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(callHistoryState.callHistory) { callHistoryItem ->
                    CallHistoryCard(
                        callHistory = callHistoryItem,
                        onCallClick = { onCallHistory(callHistoryItem) }
                    )
                }
            }
        }
        
        // Bottom Navigation
        DivoBottomNavigation(
            currentRoute = "call_history",
            onNavigate = onNavigate
        )
    }
}

@Composable
fun CallHistoryCard(
    callHistory: CallHistory,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Contact name or number
                val displayName = callHistory.contactName ?: "Unknown"
                val displayText = if (callHistory.contactName != null) {
                    "${callHistory.contactName} (${callHistory.phoneNumber})"
                } else {
                    "Unknown (${callHistory.phoneNumber})"
                }
                
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Date and time
                val dateFormat = SimpleDateFormat("M/d/yyyy, h:mm:ss a", Locale.getDefault())
                Text(
                    text = dateFormat.format(callHistory.callDate),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Call type and duration
                val callType = if (callHistory.isOutgoing) "outgoing" else "incoming"
                val durationText = formatDuration(callHistory.duration)
                Text(
                    text = "- $callType • $durationText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Call Button
            FloatingActionButton(
                onClick = onCallClick,
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call ${callHistory.contactName ?: callHistory.phoneNumber}",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        "$minutes:${String.format("%02d", remainingSeconds)}"
    } else {
        "0:${String.format("%02d", remainingSeconds)}"
    }
}

private fun addDemoCallHistory(viewModel: CallHistoryViewModel) {
    val now = Date()
    val calendar = Calendar.getInstance()
    
    // Demo call history entries
    val demoCalls = listOf(
        CallHistory(
            id = 1,
            contactName = "John Smith",
            phoneNumber = "+1-555-0123",
            callDate = Date(now.time - 3600000), // 1 hour ago
            duration = 245, // 4:05
            isOutgoing = true
        ),
        CallHistory(
            id = 2,
            contactName = "Sarah Johnson",
            phoneNumber = "+1-555-0456",
            callDate = Date(now.time - 7200000), // 2 hours ago
            duration = 180, // 3:00
            isOutgoing = true
        ),
        CallHistory(
            id = 3,
            contactName = null,
            phoneNumber = "+1-555-0789",
            callDate = Date(now.time - 10800000), // 3 hours ago
            duration = 45, // 0:45
            isOutgoing = true
        ),
        CallHistory(
            id = 4,
            contactName = "Mike Wilson",
            phoneNumber = "+1-555-0321",
            callDate = Date(now.time - 14400000), // 4 hours ago
            duration = 0, // Missed call
            isOutgoing = true
        ),
        CallHistory(
            id = 5,
            contactName = "Lisa Brown",
            phoneNumber = "+1-555-0654",
            callDate = Date(now.time - 18000000), // 5 hours ago
            duration = 320, // 5:20
            isOutgoing = true
        )
    )
    
    // Add demo calls to history
    demoCalls.forEach { callHistory ->
        viewModel.addCallHistory(callHistory)
    }
}
