package com.Fanuel.zobi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Fanuel.zobi.ui.components.DivoBottomNavigation
import com.Fanuel.zobi.ui.components.DivoHeader
import com.Fanuel.zobi.ui.components.DivoBottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialpadScreen(
    onNavigate: (DivoBottomNavItem) -> Unit,
    onCallNumber: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    
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
        
        // Phone Number Input with enhanced styling
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Enter phone number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        )
        
        // Dialpad - Centered in the middle of the screen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Row 1: 1, 2, 3
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DialpadButton("1", "", onNumberClick = { phoneNumber += "1" })
                DialpadButton("2", "ABC", onNumberClick = { phoneNumber += "2" })
                DialpadButton("3", "DEF", onNumberClick = { phoneNumber += "3" })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Row 2: 4, 5, 6
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DialpadButton("4", "GHI", onNumberClick = { phoneNumber += "4" })
                DialpadButton("5", "JKL", onNumberClick = { phoneNumber += "5" })
                DialpadButton("6", "MNO", onNumberClick = { phoneNumber += "6" })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Row 3: 7, 8, 9
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DialpadButton("7", "PQRS", onNumberClick = { phoneNumber += "7" })
                DialpadButton("8", "TUV", onNumberClick = { phoneNumber += "8" })
                DialpadButton("9", "WXYZ", onNumberClick = { phoneNumber += "9" })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Row 4: +, 0, backspace
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DialpadButton("+", "", onNumberClick = { phoneNumber += "+" })
                DialpadButton("0", "", onNumberClick = { phoneNumber += "0" })
                DialpadButton(
                    icon = Icons.Default.ArrowBack,
                    onNumberClick = { 
                        if (phoneNumber.isNotEmpty()) {
                            phoneNumber = phoneNumber.dropLast(1)
                        }
                    }
                )
            }
        }
        
        // Call Button with enhanced styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = { if (phoneNumber.isNotEmpty()) onCallNumber(phoneNumber) },
                modifier = Modifier
                    .size(88.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        
        // Bottom Navigation
        DivoBottomNavigation(
            currentRoute = "dialer",
            onNavigate = onNavigate
        )
    }
}

@Composable
fun DialpadButton(
    number: String,
    letters: String,
    onNumberClick: () -> Unit
) {
    Button(
        onClick = onNumberClick,
        modifier = Modifier
            .size(80.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DialpadButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onNumberClick: () -> Unit
) {
    Button(
        onClick = onNumberClick,
        modifier = Modifier.size(72.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Backspace",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
