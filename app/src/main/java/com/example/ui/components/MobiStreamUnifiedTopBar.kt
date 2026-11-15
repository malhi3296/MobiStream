package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MobiIconButtonShape
import com.example.ui.theme.MobiStreamTheme

/**
 * Shared Top Bar ("MobiTopBar"):
 * Logo, "MOBISTREAM", "Streaming made mobile", plus theme, filter and settings buttons (48dp touch targets, 14dp icon button radius).
 */
@Composable
fun MobiTopBar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .testTag("unified_topbar_brand")
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobistream_logo),
                contentDescription = "MobiStream Logo",
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "MOBISTREAM",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.6.sp
                    ),
                    color = colors.onBackground,
                    maxLines = 1
                )
                Text(
                    text = stringResource(id = R.string.tagline).removeSuffix("."),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TopBarIconButton(
                onClick = onToggleTheme,
                contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                icon = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                isActive = false,
                modifier = Modifier.testTag("topbar_theme_toggle")
            )

            TopBarIconButton(
                onClick = onFilterClick,
                contentDescription = "Filters",
                icon = Icons.Outlined.Tune,
                isActive = hasActiveFilters,
                modifier = Modifier.testTag("topbar_filter_button")
            )

            TopBarIconButton(
                onClick = onSettingsClick,
                contentDescription = "Settings",
                icon = Icons.Outlined.Settings,
                isActive = false,
                modifier = Modifier.testTag("topbar_settings_button")
            )
        }
    }
}

@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isActive) colors.primary else colors.onBackground,
            modifier = Modifier.size(22.dp)
        )
    }
}

/**
 * Wrapper preserving original public signature for call-site compatibility.
 */
@Composable
fun MobiStreamUnifiedTopBar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean = false,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    MobiTopBar(
        isDarkMode = isDarkMode,
        onToggleTheme = onToggleTheme,
        onSettingsClick = onSettingsClick,
        onFilterClick = onFilterClick,
        hasActiveFilters = hasActiveFilters,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MobiTopBarPreview() {
    MobiStreamTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MobiTopBar(
                isDarkMode = false,
                onToggleTheme = {},
                onSettingsClick = {},
                onFilterClick = {},
                hasActiveFilters = true
            )
        }
    }
}
