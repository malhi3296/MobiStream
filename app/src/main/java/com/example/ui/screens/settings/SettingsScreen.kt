package com.example.ui.screens.settings

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MobiButton
import com.example.ui.components.MobiButtonStyle
import com.example.ui.components.MobiCard
import com.example.ui.components.MobiChip
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    bufferMode: String,
    preferredLanguages: Set<String> = emptySet(),
    favoriteCategories: Set<String> = emptySet(),
    onToggleTheme: (Boolean) -> Unit,
    onSetBufferMode: (String) -> Unit,
    onSetPreferredLanguages: (Set<String>) -> Unit = {},
    onSetFavoriteCategories: (Set<String>) -> Unit = {},
    onExportBackupFile: () -> Unit = {},
    onImportBackupFile: () -> Unit = {},
    onClearCache: () -> Unit,
    onOpenDeveloperProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    val githubUrl = "https://github.com/malhi3296/"

    val languageOptions = remember {
        listOf("English", "Hindi", "Punjabi", "Urdu", "Tamil", "Telugu", "Bengali", "Spanish", "Arabic", "French", "German")
    }
    val categoryOptions = remember {
        listOf("News", "Sports", "Movies", "Music", "Kids", "Documentary", "Comedy", "Entertainment", "Business", "Lifestyle")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Settings & Preferences",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Configure playback, theme & app information",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            // 1. Appearance
            item {
                SettingsGroup(title = "Appearance") {
                    SettingsRowItem(
                        icon = Icons.Outlined.DarkMode,
                        title = "Midnight Dark Mode",
                        description = "Deep dark surfaces for night viewing",
                        isSelected = isDarkMode,
                        onClick = { onToggleTheme(true) }
                    )
                    HorizontalDivider(color = colors.outline)
                    SettingsRowItem(
                        icon = Icons.Outlined.LightMode,
                        title = "Cloud Light Mode",
                        description = "High-contrast crisp daytime surfaces",
                        isSelected = !isDarkMode,
                        onClick = { onToggleTheme(false) }
                    )
                }
            }

            // 2. Streaming Playback
            item {
                val isLowLatency = bufferMode.contains("low", ignoreCase = true)
                SettingsGroup(title = "Streaming Playback") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Buffering Latency",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Choose low-delay live sync or larger buffer on mobile networks.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MobiChip(
                                label = "Low Latency",
                                leadingIcon = Icons.Outlined.Speed,
                                selected = isLowLatency,
                                onClick = { onSetBufferMode("Low Latency") },
                                modifier = Modifier.weight(1f)
                            )
                            MobiChip(
                                label = "Stable Buffer",
                                leadingIcon = Icons.Outlined.Speed,
                                selected = !isLowLatency,
                                onClick = { onSetBufferMode("Stable Buffer") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Data & Storage
            item {
                SettingsGroup(title = "Data & Storage") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Refresh Channel Playlists",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Sync 11k+ iptv-org open catalog playlists",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        MobiButton(
                            text = "Sync Now",
                            style = MobiButtonStyle.TONAL,
                            onClick = {
                                onClearCache()
                                Toast.makeText(context, "Playlists up to date", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // 4. Personalization
            item {
                SettingsGroup(title = "Personalization") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Preferred Languages (${preferredLanguages.size}/5)",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            languageOptions.forEach { lang ->
                                val selected = preferredLanguages.any { it.equals(lang, ignoreCase = true) }
                                MobiChip(
                                    label = lang,
                                    selected = selected,
                                    onClick = {
                                        val next = if (selected) {
                                            if (preferredLanguages.size > 1) preferredLanguages - lang else preferredLanguages
                                        } else {
                                            if (preferredLanguages.size < 5) preferredLanguages + lang else preferredLanguages
                                        }
                                        onSetPreferredLanguages(next)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = colors.outline)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Favorite Categories",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryOptions.forEach { cat ->
                                val selected = favoriteCategories.any { it.equals(cat, ignoreCase = true) }
                                MobiChip(
                                    label = cat,
                                    selected = selected,
                                    onClick = {
                                        val next = if (selected) {
                                            if (favoriteCategories.size > 1) favoriteCategories - cat else favoriteCategories
                                        } else {
                                            favoriteCategories + cat
                                        }
                                        onSetFavoriteCategories(next)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 5. Backup & Restore
            item {
                SettingsGroup(title = "Backup & Restore") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Export or import your pinned, favorites, and settings securely on your phone. Automatic local snapshots are created once daily.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MobiButton(
                                text = "Export File",
                                style = MobiButtonStyle.PRIMARY,
                                leadingIcon = Icons.Outlined.FileUpload,
                                onClick = onExportBackupFile,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_backup_btn")
                            )
                            MobiButton(
                                text = "Import File",
                                style = MobiButtonStyle.TONAL,
                                leadingIcon = Icons.Outlined.FileDownload,
                                onClick = onImportBackupFile,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("import_backup_btn")
                            )
                        }
                    }
                }
            }

            // 6. About MobiStream
            item {
                SettingsGroup(title = "About MobiStream") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colors.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Developer Profile",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = "Arwinder Singh (@malhi3296)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            MobiButton(
                                text = "Open Link",
                                style = MobiButtonStyle.TONAL,
                                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                                    context.startActivity(intent)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = colors.outline)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = mobi.accentText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Version 1.0.6 (Build 106)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "IPTV Data Source: iptv-org",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MobiCard(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) colors.primary else colors.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) colors.onPrimary else colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = colors.onPrimary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
    MobiStreamTheme {
        SettingsScreen(
            isDarkMode = false,
            bufferMode = "Low Latency",
            preferredLanguages = setOf("English", "Hindi", "Punjabi"),
            favoriteCategories = setOf("News", "Sports", "Movies"),
            onToggleTheme = {},
            onSetBufferMode = {},
            onClearCache = {}
        )
    }
}
