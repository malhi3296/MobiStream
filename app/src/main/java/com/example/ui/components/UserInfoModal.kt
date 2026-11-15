package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.ui.theme.MobiCardShape
import com.example.ui.theme.MobiStreamTheme
import com.example.ui.theme.mobiColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = modifier.testTag("user_info_modal")
    ) {
        UserInfoModalContent(onDismiss = onDismiss)
    }
}

@Composable
fun UserInfoModalContent(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val mobi = MaterialTheme.mobiColors

    val upiId = "8288909927@airtel"
    val developerName = "Arwinder Singh"
    val githubUrl = "https://github.com/malhi3296/"
    val paypalUrl = "https://paypal.me/malhi3296"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
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
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Developer Profile",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MobiCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mobistream_logo),
                    contentDescription = developerName,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = developerName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.onSurface
                )

                Text(
                    text = "Android & Jetpack Compose Developer",
                    style = MaterialTheme.typography.bodySmall,
                    color = mobi.accentText
                )

                Spacer(modifier = Modifier.height(16.dp))

                MobiButton(
                    text = "GitHub Profile (@malhi3296)",
                    trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("github_link_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                MobiButton(
                    text = "Support via PayPal",
                    style = MobiButtonStyle.TONAL,
                    leadingIcon = Icons.Outlined.Payment,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paypal_link_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Support / Donate via UPI",
            style = MaterialTheme.typography.titleMedium,
            color = colors.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        MobiCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(200.dp),
                    shape = MobiCardShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, colors.outline)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.upi_qr_code),
                        contentDescription = "UPI Payment QR Code",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("UPI ID", upiId)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = colors.background,
                    border = BorderStroke(1.dp, colors.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
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

                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy UPI ID",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        MobiCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About MobiStream",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Version 1.0.6 (Build 106) • Streaming made mobile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = mobi.accentText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This app does not host or stream any video content directly. It acts as a player for publicly available M3U playlists provided by the open-source iptv-org project.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UserInfoModalPreview() {
    MobiStreamTheme {
        UserInfoModalContent(onDismiss = {})
    }
}
