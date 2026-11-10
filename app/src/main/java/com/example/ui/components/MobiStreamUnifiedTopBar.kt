package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary

import androidx.compose.foundation.layout.statusBarsPadding

/**
 * Unified Top Bar visible throughout the entire app:
 * - Left top: Logo and MobiStream name
 * - Right top (3 clean icons with NO background):
 *   a) Dark/Light mode toggle
 *   b) Settings
 *   c) Filter
 */
@Composable
fun MobiStreamUnifiedTopBar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDark = isDarkMode
    // High-contrast icon colors for dark and light mode
    val defaultIconTint = if (isDark) Color.White else TextDarkPrimary
    val brandTextColor = if (isDark) Color.White else TextDarkPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Top: MobiStream Brand (Logo + Wordmark)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("unified_topbar_brand")
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobistream_symbol),
                contentDescription = "MobiStream Logo",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "MOBISTREAM",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                letterSpacing = 1.8.sp,
                color = brandTextColor
            )
        }

        // Right Top Actions:
        // a) Dark/Light mode toggle (no background)
        // b) Settings icon (no background)
        // c) Filter icon (no background)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // a) Dark/Light mode toggle
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("topbar_theme_toggle")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    tint = if (isDarkMode) Color(0xFFFFD166) else defaultIconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // b) Settings
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("topbar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = defaultIconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // c) Filter
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("topbar_filter_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = if (hasActiveFilters) StreamViolet else defaultIconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
