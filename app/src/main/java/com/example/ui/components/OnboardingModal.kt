package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.StreamGradientEnd
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingModal(
    onDismiss: () -> Unit,
    onRequestPermissions: () -> Unit,
    onImportFile: () -> Unit,
    onFinish: (selectedLanguages: Set<String>, selectedCategories: Set<String>) -> Unit,
    isDark: Boolean = true,
    onToggleTheme: (Boolean) -> Unit = {}
) {
    // Step 1: Theme (Dark/Light), Step 2: Languages, Step 3: Categories, Step 4: Permissions & Restore
    var step by remember { mutableIntStateOf(1) }
    var selectedLangs by remember { mutableStateOf(setOf("Punjabi", "English", "Hindi")) }
    var selectedCats by remember { mutableStateOf(setOf("News", "Sports", "Movies", "Music")) }

    val availableLangs = listOf(
        "Punjabi" to "ਅ",
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

    val surfaceBg = if (isDark) Color(0xFF3F3F46) else Color(0xFFD4D4D8)
    val cardBg = if (isDark) Color(0xFF27272A) else Color(0xFFE4E4E7)
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
                // Top Clean Logo Icon (No white box around it)
                Image(
                    painter = painterResource(id = R.drawable.ic_mobistream_symbol),
                    contentDescription = "MobiStream Logo",
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Top Step Progress Indicator (4 Steps)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..4) {
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

                Spacer(modifier = Modifier.height(20.dp))

                when (step) {
                    1 -> {
                        // Step 1: Choose Dark Mode or Light Mode
                        Text(
                            text = "Choose App Appearance",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Select your preferred theme for streaming live television.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = secondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
                        )

                        // Dark Mode Option Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isDark) 2.dp else 1.dp,
                                    color = if (isDark) StreamViolet else (if (isDark) Color(0xFF2C2266) else Color(0xFFDCDFEA)),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onToggleTheme(true) },
                            color = if (isDark) Color(0xFF1E1748) else Color(0xFFF5F4FA)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) StreamViolet else Color(0xFFE2E4F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Brightness4,
                                        contentDescription = "Dark Mode",
                                        tint = if (isDark) Color.White else Color(0xFF555070),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Dark Mode (Recommended)",
                                            fontFamily = InterFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = primaryText
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Midnight purple styling optimized for low-light streaming",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        color = secondaryText
                                    )
                                }
                                if (isDark) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = StreamViolet,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Light Mode Option Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (!isDark) 2.dp else 1.dp,
                                    color = if (!isDark) StreamViolet else (if (isDark) Color(0xFF2C2266) else Color(0xFFDCDFEA)),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onToggleTheme(false) },
                            color = if (!isDark) Color(0xFFEDE9FE) else cardBg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (!isDark) StreamViolet else Color(0xFF282052)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Brightness7,
                                        contentDescription = "Light Mode",
                                        tint = if (!isDark) Color.White else Color(0xFFA19BD0),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Light Mode",
                                        fontFamily = InterFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = primaryText
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "High-contrast clean and crisp daytime interface",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        color = secondaryText
                                    )
                                }
                                if (!isDark) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = StreamViolet,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(
                            onClick = { step = 2 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_next_step_0"),
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
                        // Step 1: Select Preferred Languages (Up to 5)
                        Text(
                            text = "Choose Preferred Languages",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Pick up to 5 languages. Live TV streams in your chosen languages will be prioritized at the top of your feed.",
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

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = { step = 3 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_next_step_1"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Text(
                                text = "Next: Choose Categories (${selectedLangs.size}/5)",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    3 -> {
                        // Step 3: Select Favorite Categories
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
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = StreamViolet,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = cat,
                                            fontFamily = InterFontFamily,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.5.sp,
                                            color = if (isSelected) primaryText else secondaryText
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = { step = 4 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("onboarding_next_step_2"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StreamViolet)
                        ) {
                            Text(
                                text = "Next: Permissions & Backup",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }

                    4 -> {
                        // Step 4: Permissions & Existing Backup Check
                        Text(
                            text = "Ready to Stream",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = primaryText,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Enable device permissions for best experience, or restore your previous channel lists.",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = secondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                        )

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
                                    fontSize = 14.sp,
                                    color = primaryText
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, tint = StreamViolet, modifier = Modifier.size(18.dp))
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
                                    Icon(Icons.Default.Notifications, null, tint = StreamViolet, modifier = Modifier.size(18.dp))
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
                                    fontSize = 14.sp,
                                    color = primaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Restore your pinned & favorite channels or continue fresh.",
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = secondaryText
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { onImportFile() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.FileOpen, null, modifier = Modifier.size(16.dp), tint = StreamViolet)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Import Existing Backup (.json)", fontSize = 12.sp, fontFamily = InterFontFamily, color = primaryText)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                onRequestPermissions()
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
