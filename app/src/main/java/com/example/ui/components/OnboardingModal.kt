package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.MobiStreamTheme

@Composable
fun OnboardingModal(
    onDismiss: () -> Unit,
    onRequestPermissions: () -> Unit,
    onImportFile: () -> Unit,
    onFinish: (selectedLanguages: Set<String>, selectedCategories: Set<String>) -> Unit,
    isDark: Boolean = true,
    onToggleTheme: (Boolean) -> Unit = {}
) {
    var step by remember { mutableIntStateOf(1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        MobiStreamTheme(darkTheme = isDark) {
            OnboardingContent(
                step = step,
                onStepChange = { step = it },
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                onRequestPermissions = onRequestPermissions,
                onImportFile = onImportFile,
                onFinish = onFinish
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingContent(
    step: Int,
    onStepChange: (Int) -> Unit,
    isDark: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onRequestPermissions: () -> Unit,
    onImportFile: () -> Unit,
    onFinish: (selectedLanguages: Set<String>, selectedCategories: Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    var selectedLangs by remember { mutableStateOf(setOf("Punjabi", "English", "Hindi")) }
    var selectedCats by remember { mutableStateOf(setOf("News", "Sports", "Movies", "Music")) }

    val availableLangs = remember {
        listOf(
            "Hindi" to "अ",
            "English" to "Aa",
            "Punjabi" to "ਅ",
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
    }

    val availableCats = remember {
        listOf(
            "Music", "Movies", "Sports", "News", "Series", "Animation", "Kids",
            "Documentary", "Comedy", "Entertainment", "Education", "Auto",
            "Business", "Lifestyle", "Science", "Travel", "Cooking", "Weather"
        )
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("onboarding_modal"),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mobistream_logo),
                contentDescription = "MobiStream Logo",
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4-step progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 1..4) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(6.dp)
                            .width(if (step == i) 30.dp else 10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (step >= i) colors.primary else colors.outline)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> {
                    Text(
                        text = "Choose App Appearance",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Select your preferred theme for streaming live television.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
                    )

                    MobiCard(
                        selected = isDark,
                        onClick = { onToggleTheme(true) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) colors.primary else colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DarkMode,
                                    contentDescription = "Dark Mode",
                                    tint = if (isDark) colors.onPrimary else colors.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dark Mode (Recommended)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Midnight purple styling optimized for low-light streaming",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            if (isDark) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = colors.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    MobiCard(
                        selected = !isDark,
                        onClick = { onToggleTheme(false) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (!isDark) colors.primary else colors.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LightMode,
                                    contentDescription = "Light Mode",
                                    tint = if (!isDark) colors.onPrimary else colors.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Light Mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "High-contrast clean and crisp daytime interface",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                            if (!isDark) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = colors.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    MobiButton(
                        text = "Next: Choose Languages",
                        onClick = { onStepChange(2) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_next_step_0")
                    )
                }

                2 -> {
                    Text(
                        text = "Choose Preferred Languages",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Pick up to 5 languages. Live TV streams in your chosen languages will be prioritized at the top of your feed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
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
                            MobiChip(
                                label = name,
                                leadingGlyph = glyph,
                                selected = isSelected,
                                onClick = {
                                    selectedLangs = if (isSelected) {
                                        if (selectedLangs.size > 1) selectedLangs - name else selectedLangs
                                    } else {
                                        if (selectedLangs.size < 5) selectedLangs + name else selectedLangs
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    MobiButton(
                        text = "Next: Choose Categories (${selectedLangs.size}/5)",
                        onClick = { onStepChange(3) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_next_step_1")
                    )
                }

                3 -> {
                    Text(
                        text = "What do you love watching?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Select your favorite genres. We'll curate the \"Channels You May Like\" carousel based on these.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
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
                            MobiChip(
                                label = cat,
                                leadingIcon = if (isSelected) Icons.Default.Check else null,
                                selected = isSelected,
                                onClick = {
                                    selectedCats = if (isSelected) {
                                        if (selectedCats.size > 1) selectedCats - cat else selectedCats
                                    } else {
                                        selectedCats + cat
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    MobiButton(
                        text = "Next: Permissions & Backup",
                        onClick = { onStepChange(4) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_next_step_2")
                    )
                }

                4 -> {
                    Text(
                        text = "Ready to Stream",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Enable device permissions for best experience, or restore your previous channel lists.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )

                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Device Permissions",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Location: Automatically shows top TV channels from your country",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Notifications: Stream updates and scheduled sports / live events",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MobiCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Have an existing backup?",
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Restore your pinned & favorite channels or continue fresh.",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            MobiButton(
                                text = "Import Existing Backup (.json)",
                                style = MobiButtonStyle.TONAL,
                                leadingIcon = Icons.Outlined.FileOpen,
                                onClick = onImportFile,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    MobiButton(
                        text = "Start Watching MobiStream",
                        onClick = {
                            onRequestPermissions()
                            onFinish(selectedLangs, selectedCats)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_finish_button")
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OnboardingStep1Preview() {
    MobiStreamTheme {
        OnboardingContent(
            step = 1,
            onStepChange = {},
            isDark = false,
            onToggleTheme = {},
            onRequestPermissions = {},
            onImportFile = {},
            onFinish = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OnboardingStep2Preview() {
    MobiStreamTheme {
        OnboardingContent(
            step = 2,
            onStepChange = {},
            isDark = true,
            onToggleTheme = {},
            onRequestPermissions = {},
            onImportFile = {},
            onFinish = { _, _ -> }
        )
    }
}
