package com.example.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LiveRed
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Requirement: Make different page for profile instead of modal with same content.
 */
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
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF3F3F46)

    val upiId = "8288909927@airtel"
    val developerName = "Arwinder Singh"
    val githubUrl = "https://github.com/malhi3296/"
    val paypalUrl = "https://paypal.me/malhi3296"

    val primaryTextColor = if (isDark) TextWhite else TextDarkPrimary
    val secondaryTextColor = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary
    val cardBg = if (isDark) MidnightCard else Color(0xFFF3F4F9)
    val cardBorder = if (isDark) Color(0xFF2E2460) else Color(0xFFDCDFEA)

    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Screen Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF221A44) else Color(0xFFE4E7F5))
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = primaryTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Developer Profile",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = primaryTextColor
                    )
                    Text(
                        text = "MobiStream Creator & App Information",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Developer Main Identity Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(StreamViolet, Color(0xFF4F2BC4))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AS",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = developerName,
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                        color = primaryTextColor
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Android & Streaming Systems Engineer",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = secondaryTextColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // UPI Quick Copy Tile
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, if (isDark) Color(0xFF3B2E76) else Color(0xFFD6DAE8), RoundedCornerShape(14.dp))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("UPI ID", upiId)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                            },
                        color = if (isDark) Color(0xFF160F34) else Color(0xFFFFFFFF)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(LiveRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = LiveRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "UPI Payment ID",
                                        fontFamily = InterFontFamily,
                                        fontSize = 11.sp,
                                        color = secondaryTextColor
                                    )
                                    Text(
                                        text = upiId,
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = primaryTextColor
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy UPI ID",
                                tint = StreamViolet,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // External Action Buttons: GitHub & PayPal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "GitHub",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                color = primaryTextColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = secondaryTextColor
                            )
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Text(
                                text = "PayPal",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Support & UPI QR Code Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan & Support via UPI",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = primaryTextColor
                    )
                    Text(
                        text = "Scan with Google Pay, PhonePe, Paytm, or any UPI app",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = secondaryTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // QR Code Image from local asset
                    Surface(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, if (isDark) Color(0xFF382A6B) else Color(0xFFD6DAE8), RoundedCornerShape(16.dp)),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.upi_qr_code),
                            contentDescription = "UPI Payment QR Code for Arwinder Singh",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "UPI: $upiId",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Statistics Cards
            Text(
                text = "Library Overview",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = secondaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Channels",
                    value = if (totalChannelsCount > 0) "$totalChannelsCount" else "11,000+",
                    icon = Icons.Default.Tv,
                    iconTint = StreamViolet,
                    cardBg = cardBg,
                    primaryText = primaryTextColor,
                    secondaryText = secondaryTextColor,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Favorites",
                    value = "$favoriteChannelsCount",
                    icon = Icons.Default.Favorite,
                    iconTint = LiveRed,
                    cardBg = cardBg,
                    primaryText = primaryTextColor,
                    secondaryText = secondaryTextColor,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pinned",
                    value = "$pinnedChannelsCount",
                    icon = Icons.Default.PushPin,
                    iconTint = Color(0xFFFFB300),
                    cardBg = cardBg,
                    primaryText = primaryTextColor,
                    secondaryText = secondaryTextColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Device Backup & Restore Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Device Backup & Restore",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = primaryTextColor
                    )
                    Text(
                        text = "Automatic local snapshots are created once daily. You can also export or import your channel lists as JSON files anytime.",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExportBackup,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Backup", fontSize = 12.sp, fontFamily = InterFontFamily)
                        }

                        OutlinedButton(
                            onClick = onImportBackup,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileOpen, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Backup", fontSize = 12.sp, fontFamily = InterFontFamily, color = primaryTextColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About MobiStream Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = StreamViolet,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "About MobiStream",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = primaryTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "MobiStream delivers 11,000+ free-to-air live television streams sourced from the global iptv-org open catalog. Featuring gesture volume and brightness control, automatic daily local backup, category folders, closed captions, and seamless light/dark styling.",
                        fontFamily = InterFontFamily,
                        fontSize = 12.5.sp,
                        color = secondaryTextColor,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Version 1.0.4 (Build 104) • Streaming made mobile.",
                        fontFamily = InterFontFamily,
                        fontSize = 11.5.sp,
                        color = StreamViolet,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    cardBg: Color,
    primaryText: Color,
    secondaryText: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = cardBg
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = primaryText
            )
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                color = secondaryText
            )
        }
    }
}
