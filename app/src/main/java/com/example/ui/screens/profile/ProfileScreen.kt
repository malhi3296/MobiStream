package com.example.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.MobiButton
import com.example.ui.components.MobiButtonStyle
import com.example.ui.components.MobiCard
import com.example.ui.theme.MobiCardShape
import com.example.ui.theme.MobiIconButtonShape
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors

@Composable
fun ProfileScreen(
    totalChannelsCount: Int,
    favoriteChannelsCount: Int,
    pinnedChannelsCount: Int,
    onBack: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    val upiId = "8288909927@airtel"
    val developerName = "Arwinder Singh"
    val githubUrl = "https://github.com/malhi3296/"
    val paypalUrl = "https://paypal.me/malhi3296"

    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Developer Identity Card
            item {
                MobiCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mobistream_logo),
                                contentDescription = "Developer Logo",
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = developerName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = colors.onSurface
                                )
                                Text(
                                    text = "Android & Streaming Systems Engineer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = mobi.accentText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MobiButton(
                                text = "GitHub",
                                style = MobiButtonStyle.TONAL,
                                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            MobiButton(
                                text = "PayPal",
                                style = MobiButtonStyle.TONAL,
                                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 2. Library Overview
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Library Overview",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OverviewStatItem(
                                count = totalChannelsCount.toString(),
                                label = "Channels",
                                modifier = Modifier.weight(1f)
                            )
                            OverviewStatItem(
                                count = favoriteChannelsCount.toString(),
                                label = "Favorites",
                                modifier = Modifier.weight(1f)
                            )
                            OverviewStatItem(
                                count = pinnedChannelsCount.toString(),
                                label = "Pinned",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 3. Scan & Support via UPI
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Scan & Support via UPI",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    modifier = Modifier.size(96.dp),
                                    shape = MobiCardShape,
                                    color = Color.White,
                                    border = BorderStroke(1.dp, colors.outline)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.upi_qr_code),
                                        contentDescription = "UPI QR Code",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Scan with Google Pay, PhonePe, Paytm, or any UPI app",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.onSurfaceVariant,
                                        lineHeight = 17.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "UPI ID",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.onSurfaceVariant
                                    )
                                    Text(
                                        text = upiId,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = colors.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MobiButton(
                                    text = "Copy UPI ID",
                                    style = MobiButtonStyle.TONAL,
                                    leadingIcon = Icons.Outlined.ContentCopy,
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("UPI ID", upiId)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                MobiButton(
                                    text = "Open Link",
                                    style = MobiButtonStyle.PRIMARY,
                                    trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                                    onClick = {
                                        try {
                                            val uri = Uri.parse("upi://pay?pa=$upiId&pn=Arwinder%20Singh&cu=INR")
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                                            Toast.makeText(context, "UPI ID copied", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Device Backup & Restore
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Device Backup & Restore",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Export or import your pinned, favorites, and settings securely on your phone. Automatic local snapshots are created once daily. You can also export or import your channel lists as JSON files anytime.",
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
                                    text = "Export Backup",
                                    style = MobiButtonStyle.PRIMARY,
                                    leadingIcon = Icons.Outlined.FileUpload,
                                    onClick = onExportBackup,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("export_backup_button")
                                )
                                MobiButton(
                                    text = "Import Backup",
                                    style = MobiButtonStyle.TONAL,
                                    leadingIcon = Icons.Outlined.FileDownload,
                                    onClick = onImportBackup,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("import_backup_button")
                                )
                            }
                        }
                    }
                }
            }

            // 5. About MobiStream
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "About MobiStream",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Version 1.0.6 (Build 106) • Streaming made mobile.",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = mobi.accentText
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "This app does not host or stream any video content directly. It acts as a player for publicly available M3U playlists provided by the open-source iptv-org project.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewStatItem(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = mobi.accentText
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileScreenPreview() {
    MobiStreamTheme {
        ProfileScreen(
            totalChannelsCount = 11420,
            favoriteChannelsCount = 14,
            pinnedChannelsCount = 4,
            onBack = {},
            onExportBackup = {},
            onImportBackup = {}
        )
    }
}
