package com.example.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Tune
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.OutlinedButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    bufferMode: String,
    preferredLanguages: Set<String> = emptySet(),
    favoriteCategories: Set<String> = emptySet(),
    isGoogleDriveBackupEnabled: Boolean = false,
    googleDriveLastBackupTime: Long = 0L,
    onToggleTheme: (Boolean) -> Unit,
    onSetBufferMode: (String) -> Unit,
    onSetPreferredLanguages: (Set<String>) -> Unit = {},
    onSetFavoriteCategories: (Set<String>) -> Unit = {},
    onToggleGoogleDriveBackup: (Boolean) -> Unit = {},
    onBackupToDrive: () -> Unit = {},
    onRestoreFromDrive: () -> Unit = {},
    onExportBackupFile: () -> Unit = {},
    onImportBackupFile: () -> Unit = {},
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cacheClearedMessage by remember { mutableStateOf(false) }
    var backupStatusMessage by remember { mutableStateOf("") }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    val isDark = MaterialTheme.colorScheme.background != Color.White
    val titleTextColor = if (isDark) TextWhite else TextDarkPrimary
    val subtitleTextColor = if (isDark) TextMuted else TextDarkSecondary
    val cardBg = if (isDark) MidnightCard else Color(0xFFF6F7FC)
    val cardBorder = if (isDark) MidnightCardBorder else Color(0xFFE2E4EE)
    val innerOptionBg = if (isDark) Color(0xFF130E30) else Color(0xFFECEEF7)
    val iconBoxBg = if (isDark) Color(0xFF281E57) else Color(0xFFEFE9FE)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("settings_screen")
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Settings & Preferences",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = titleTextColor
            )
            Text(
                text = "Configure playback, theme & app information",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = subtitleTextColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Theme Setting Section
            SettingsSectionHeader(title = "Appearance", isDark = isDark)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(iconBoxBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDark) GlowLilac else StreamViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isDarkMode) "Midnight Dark Mode" else "Cloud Light Mode",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = titleTextColor
                            )
                            Text(
                                text = if (isDarkMode) "#0E0A24 stream background" else "Clean lavender light surfaces",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = subtitleTextColor
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = StreamViolet,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = if (isDark) Color(0xFF281E57) else Color(0xFFCCCCCC)
                        ),
                        modifier = Modifier.testTag("theme_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Playback / Stream Settings
            SettingsSectionHeader(title = "Streaming Playback", isDark = isDark)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = if (isDark) GlowLilac else StreamViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Buffering Latency",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = titleTextColor
                            )
                            Text(
                                text = "Balance between live speed and playback stability",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = subtitleTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Low Latency", "Stable Buffer").forEach { mode ->
                            val isSelected = bufferMode == mode
                            val shape = RoundedCornerShape(12.dp)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(shape)
                                    .background(if (isSelected) StreamViolet else innerOptionBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) StreamGradientStart else cardBorder,
                                        shape
                                    )
                                    .clickable { onSetBufferMode(mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = mode,
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else subtitleTextColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Cache & Playlist Management
            SettingsSectionHeader(title = "Data & Storage", isDark = isDark)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = null,
                            tint = if (isDark) GlowLilac else StreamViolet,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Refresh Channel Playlists",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = titleTextColor
                            )
                            Text(
                                text = if (cacheClearedMessage) "Refreshing playlist registry..." else "Sync 11k+ iptv-org playlists",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = if (cacheClearedMessage) (if (isDark) GlowLilac else StreamViolet) else subtitleTextColor
                            )
                        }
                    }

                    Button(
                        onClick = {
                            cacheClearedMessage = true
                            onClearCache()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StreamViolet
                        ),
                        modifier = Modifier.testTag("refresh_cache_button")
                    ) {
                        Text(
                            text = "Sync Now",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Personalization (Preferred Languages & Favorite Categories)
            SettingsSectionHeader(title = "Personalization", isDark = isDark)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, null, tint = if (isDark) GlowLilac else StreamViolet, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Preferred Languages (up to 5)", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = titleTextColor)
                                Text("${preferredLanguages.size} selected • Top priority in feeds", fontFamily = InterFontFamily, fontSize = 12.sp, color = subtitleTextColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Punjabi", "Hindi", "English", "Urdu", "Tamil", "Telugu", "Bengali", "Spanish", "Arabic").forEach { lang ->
                            val isSelected = preferredLanguages.contains(lang)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) StreamViolet else innerOptionBg)
                                    .border(1.dp, if (isSelected) StreamGradientStart else cardBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val updated = if (isSelected) {
                                            if (preferredLanguages.size > 1) preferredLanguages - lang else preferredLanguages
                                        } else {
                                            if (preferredLanguages.size < 5) preferredLanguages + lang else preferredLanguages
                                        }
                                        onSetPreferredLanguages(updated)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = lang,
                                    fontFamily = InterFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else subtitleTextColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, null, tint = if (isDark) GlowLilac else StreamViolet, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Favorite Categories", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = titleTextColor)
                                Text("Curates 'Channels You May Like'", fontFamily = InterFontFamily, fontSize = 12.sp, color = subtitleTextColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("News", "Sports", "Movies", "Series", "Music", "Animation", "Kids", "Documentary", "Comedy").forEach { cat ->
                            val isSelected = favoriteCategories.contains(cat)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) StreamViolet else innerOptionBg)
                                    .border(1.dp, if (isSelected) StreamGradientStart else cardBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val updated = if (isSelected) {
                                            if (favoriteCategories.size > 1) favoriteCategories - cat else favoriteCategories
                                        } else {
                                            favoriteCategories + cat
                                        }
                                        onSetFavoriteCategories(updated)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontFamily = InterFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else subtitleTextColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Backup & Cloud Sync (Google Drive & JSON File)
            SettingsSectionHeader(title = "Backup & Restore", isDark = isDark)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    // Google Drive Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(CircleShape).background(iconBoxBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudUpload, null, tint = if (isDark) GlowLilac else StreamViolet, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Google Drive Cloud Backup", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = titleTextColor)
                                Text(
                                    if (googleDriveLastBackupTime > 0) "Synced: ${java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(googleDriveLastBackupTime))}"
                                    else "Save favorites & pinned across devices",
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = subtitleTextColor
                                )
                            }
                        }

                        Switch(
                            checked = isGoogleDriveBackupEnabled,
                            onCheckedChange = { onToggleGoogleDriveBackup(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = StreamViolet,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = if (isDark) Color(0xFF281E57) else Color(0xFFCCCCCC)
                            )
                        )
                    }

                    if (isGoogleDriveBackupEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onBackupToDrive()
                                    backupStatusMessage = "Backed up to Google Drive!"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                            ) {
                                Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Backup Now", fontFamily = InterFontFamily, fontSize = 12.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    onRestoreFromDrive()
                                    backupStatusMessage = "Restored from Google Drive!"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restore", fontFamily = InterFontFamily, fontSize = 12.sp, color = titleTextColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Local File Export / Import
                    Text("Local Backup File (.json)", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = titleTextColor)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Export or import your channel lists and preferences as a portable JSON file.", fontFamily = InterFontFamily, fontSize = 12.sp, color = subtitleTextColor)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onExportBackupFile,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export File", fontFamily = InterFontFamily, fontSize = 12.sp, color = titleTextColor)
                        }

                        OutlinedButton(
                            onClick = onImportBackupFile,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import File", fontFamily = InterFontFamily, fontSize = 12.sp, color = titleTextColor)
                        }
                    }

                    if (backupStatusMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = backupStatusMessage,
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isDark) GlowLilac else StreamViolet
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Developer & About
            SettingsSectionHeader(title = "About MobiStream", isDark = isDark)
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_mobistream_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MobiStream",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = titleTextColor
                            )
                            Text(
                                text = "Streaming made mobile",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isDark) GlowLilac else StreamViolet
                            )
                            Text(
                                text = "Version 1.0.2 (Build 102)",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = subtitleTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // GitHub Profile Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(innerOptionBg)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/malhi3296/"))
                                context.startActivity(intent)
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = if (isDark) GlowLilac else StreamViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Developer Profile",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = titleTextColor
                                )
                                Text(
                                    text = "https://github.com/malhi3296/",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = subtitleTextColor
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open Link",
                            tint = if (isDark) GlowLilac else StreamViolet,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Open Source IPTV registry attribution
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(innerOptionBg)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/iptv-org/iptv"))
                                context.startActivity(intent)
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isDark) GlowLilac else StreamViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "IPTV Data Source",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = titleTextColor
                                )
                                Text(
                                    text = "Powered by iptv-org open source community",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = subtitleTextColor
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open Link",
                            tint = if (isDark) GlowLilac else StreamViolet,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, isDark: Boolean) {
    Text(
        text = title,
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = if (isDark) GlowLilac else StreamViolet
    )
}

@Composable
private fun SettingsCard(
    cardBg: Color,
    cardBorder: Color,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorder, shape),
        shape = shape,
        color = cardBg
    ) {
        content()
    }
}
