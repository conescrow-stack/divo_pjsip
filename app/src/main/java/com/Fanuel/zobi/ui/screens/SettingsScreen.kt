package com.Fanuel.zobi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Fanuel.zobi.data.model.AudioCodec
import com.Fanuel.zobi.ui.components.DivoBottomNavigation
import com.Fanuel.zobi.ui.components.DivoHeader
import com.Fanuel.zobi.ui.components.DivoBottomNavItem
import com.Fanuel.zobi.ui.viewmodel.LoginViewModel

@Composable
fun SettingsScreen(
    onNavigate: (DivoBottomNavItem) -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val sipConfig by viewModel.sipConfig.collectAsState()
    
    // Track enabled codecs (all enabled by default)
    var enabledCodecs by remember { 
        mutableStateOf(AudioCodec.values().toSet()) 
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
        
        // Settings Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Audio Settings Title
            Text(
                text = "Audio settings",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Audio Codec Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Audio Codecs (All Enabled)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    AudioCodec.values().forEach { codec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = enabledCodecs.contains(codec),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        enabledCodecs = enabledCodecs + codec
                                    } else {
                                        enabledCodecs = enabledCodecs - codec
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                            Text(
                                text = codec.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // SIP Configuration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "SIP Configuration",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Username
                    Text(
                        text = "Username: ${sipConfig.username.ifEmpty { "Not set" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Domain
                    Text(
                        text = "SIP Domain: ${sipConfig.domain.ifEmpty { "Not set" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Remember Me Status
                    Text(
                        text = "Remember Me: ${if (sipConfig.rememberMe) "Enabled" else "Disabled"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Bottom Navigation
        DivoBottomNavigation(
            currentRoute = "settings",
            onNavigate = onNavigate
        )
    }
}
