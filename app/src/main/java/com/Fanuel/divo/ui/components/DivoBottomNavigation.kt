package com.Fanuel.divo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class DivoBottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    CONTACTS("Contacts", Icons.Default.Person),
    DIALPAD("Dialpad", Icons.Default.Phone),
    HISTORY("History", Icons.Default.List),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun DivoBottomNavigation(
    currentRoute: String,
    onNavigate: (DivoBottomNavItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        DivoBottomNavItem.values().forEach { item ->
            val isSelected = when (item) {
                DivoBottomNavItem.CONTACTS -> currentRoute == "contacts"
                DivoBottomNavItem.DIALPAD -> currentRoute == "dialer"
                DivoBottomNavItem.HISTORY -> currentRoute == "call_history"
                DivoBottomNavItem.SETTINGS -> currentRoute == "settings"
            }
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary 
                              else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = { onNavigate(item) }
            )
        }
    }
}
