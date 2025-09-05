package com.Fanuel.divo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Fanuel.divo.R
import com.Fanuel.divo.ui.theme.DivoNeonGreen

@Composable
fun DivoHeader(
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    showSettings: Boolean = true,
    showLogout: Boolean = true,
    logoSize: Dp = 405.dp // Default size for login screen
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Settings - Positioned on the left (only if showSettings is true)
        if (showSettings) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        // DIVO Logo with PNG image - Centered
        Image(
            painter = painterResource(id = R.drawable.divo_red_title),
            contentDescription = "DIVO Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = logoSize, height = (logoSize.value * 0.333f).dp) // Maintain aspect ratio
        )
        
        // Logout - Positioned on the right (only if showLogout is true)
        if (showLogout) {
            TextButton(
                onClick = onLogoutClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    text = "Logout",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
