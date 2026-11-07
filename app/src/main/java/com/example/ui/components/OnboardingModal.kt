package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.StreamGradientEnd
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingModal(
    onDismiss: () -> Unit,
    onRequestPermissions: () -> Unit,
    onRestoreDrive: () -> Unit,
    onImportFile: () -> Unit,
    onFinish: (selectedLanguages: Set<String>, selectedCategories: Set<String>) -> Unit,
    isDark: Boolean = true
) {
    var step by remember { mutableIntStateOf(1) } // 1: Permissions & Restore, 2: Languages, 3: Categories
    var selectedLangs by remember { mutableStateOf(setOf("Punjabi", "English", "Hindi")) }
    var selectedCats by remember { mutableStateOf(setOf("News", "Sports", "Movies", "Music")) }

    val availableLangs = listOf(
        "Punjabi" to "ੴ",
        "Hindi" to "अ",
        "English" to "Aa",
        "Urdu" to "ا",
        "Tamil" to "அ",
        "Telugu" to "అ",
        "Bengali" to "অ",
        "Malayalam" to "അ",
        "Kannada" to "ಅ",
        "Marathi" to "म",
        "Gujarati" to "ગુ",
        "Arabic" to "ض",
        "Spanish" to "ñ",
        "French" to "Fr",
        "German" to "De",
        "Russian" to "Я"
    )

    val availableCats = listOf(
        "News", "Sports", "Movies", "Series", "Music", "Animation", "Kids",
        "Documentary", "Comedy", "Entertainment", "Education", "Auto",
        "Business", "Lifestyle", "Science", "Travel", "Cooking", "Weather"
    )

    val surfaceBg = if (isDark) Color(0xFF130E2E) else Color(0xFFFFFFFF)
    val cardBg = if (isDark) Color(0xFF1C1542) else Color(0xFFF3F4F9)
    val primaryText = if (isDark) Color.White else TextDarkPrimary
    val secondaryText = if (isDark) Color(0xFFA19BD0) else TextDarkSecondary

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceBg)
                .testTag("onboarding_modal"),
            color = surfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..3) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(5.dp)
                                .width(if (step == i) 28.dp else 10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (step >= i) StreamViolet else Color(0x33A19BD0))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (step) {
                    1 -> {
                        // Step 1: Welcome, Permissions & Backup Check
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(StreamGradientStart, StreamGradientEnd))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Welcome to MobiStream",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Streaming made mobile. Personalize your experience and check for existing backups.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.5.sp,
                            color = secondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Permissions Notice
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Device Permissions",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = primaryText
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, tint = StreamViolet, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Location: Automatically shows top TV channels from your country",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        color = secondaryText
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, null, tint = StreamViolet, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Notifications: Stream updates and scheduled sports / live events",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        color = secondaryText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Backup Check
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Have an existing backup?",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = primaryText
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Restore your pinned & favorite channels or continue fresh.",
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = secondaryText
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onRestoreDrive() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.CloudDownload, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Google Drive", fontSize = 11.sp, fontFamily = InterFontFamily, color = primaryText)
                                    }

                                    OutlinedButton(
                                        onClick = { onImportFile() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.FileOpen, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Import File", fontSize = 11.sp, fontFamily = InterFontFamily, color = primaryText)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                onRequestPermissions()
                                step = 2
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_next_step_1"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Text(
                                text = "Next: Choose Languages",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    2 -> {
                        // Step 2: Preferred Languages (Up to 5)
                        Text(
                            text = "Select Preferred Languages",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Pick up to 5 languages. Channels in your chosen languages will be prioritized at the top of your feed.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = secondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableLangs.forEach { (name, glyph) ->
                                val isSelected = selectedLangs.contains(name)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) StreamViolet else (if (isDark) Color(0xFF2C2266) else Color(0xFFDCDFEA)),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            if (isSelected) {
                                                if (selectedLangs.size > 1) selectedLangs = selectedLangs - name
                                            } else {
                                                if (selectedLangs.size < 5) selectedLangs = selectedLangs + name
                                            }
                                        },
                                    color = if (isSelected) (if (isDark) Color(0xFF322268) else Color(0xFFEBE6FF)) else cardBg
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = glyph,
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isSelected) StreamViolet else secondaryText
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = name,
                                            fontFamily = InterFontFamily,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (isSelected) primaryText else secondaryText
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { step = 3 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_next_step_2"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Text(
                                text = "Next: Interest Categories (${selectedLangs.size}/5)",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    3 -> {
                        // Step 3: Favorite Categories
                        Text(
                            text = "What do you love watching?",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Select your favorite genres. We'll curate the \"Channels You May Like\" carousel based on these.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = secondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableCats.forEach { cat ->
                                val isSelected = selectedCats.contains(cat)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) StreamViolet else (if (isDark) Color(0xFF2C2266) else Color(0xFFDCDFEA)),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            selectedCats = if (isSelected) {
                                                if (selectedCats.size > 1) selectedCats - cat else selectedCats
                                            } else {
                                                selectedCats + cat
                                            }
                                        },
                                    color = if (isSelected) (if (isDark) Color(0xFF322268) else Color(0xFFEBE6FF)) else cardBg
                                ) {
                                    Text(
                                        text = cat,
                                        fontFamily = InterFontFamily,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.5.sp,
                                        color = if (isSelected) primaryText else secondaryText,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                onFinish(selectedLangs, selectedCats)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_finish_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Text(
                                text = "Start Watching MobiStream",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
