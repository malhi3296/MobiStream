package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.domain.FilterOptions
import com.example.ui.theme.GlowLilac
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamViolet
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterOptions: FilterOptions,
    categories: List<Category>,
    countries: List<Country>,
    languages: List<Language>,
    onApplyFilters: (FilterOptions) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = MaterialTheme.colorScheme.background != Color.White

    val containerBg = if (isDark) MidnightBackground else Color.White
    val titleTextColor = if (isDark) TextWhite else TextDarkPrimary
    val sectionHeaderColor = if (isDark) GlowLilac else StreamViolet
    val closeIconTint = if (isDark) TextMuted else TextDarkSecondary

    var tempCategory by remember { mutableStateOf(filterOptions.selectedCategory) }
    var tempCountry by remember { mutableStateOf(filterOptions.selectedCountry) }
    var tempLanguage by remember { mutableStateOf(filterOptions.selectedLanguage) }
    var tempQuality by remember { mutableStateOf(filterOptions.selectedQuality) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = containerBg,
        dragHandle = null,
        modifier = modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = null,
                        tint = sectionHeaderColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Filter Channels",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = titleTextColor
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = closeIconTint
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Categories Filter (Requirement: "only use Category instead of genre/category")
            Text(
                text = "Category",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = sectionHeaderColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val allCats = listOf("All") + categories.map { it.name }.distinct()
                allCats.forEach { cat ->
                    val isSelected = tempCategory.equals(cat, ignoreCase = true)
                    FilterChipItem(
                        text = cat,
                        isSelected = isSelected,
                        isDark = isDark,
                        onClick = { tempCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Country Filter
            Text(
                text = "Country",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = sectionHeaderColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChipItem(
                    text = "🌐 All Countries",
                    isSelected = tempCountry == "All",
                    isDark = isDark,
                    onClick = { tempCountry = "All" }
                )
                countries.take(16).forEach { country ->
                    val isSelected = tempCountry.equals(country.code, ignoreCase = true) ||
                            tempCountry.equals(country.name, ignoreCase = true)
                    FilterChipItem(
                        text = "${country.flag} ${country.name}",
                        isSelected = isSelected,
                        isDark = isDark,
                        onClick = { tempCountry = country.code }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Language Filter
            Text(
                text = "Language",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = sectionHeaderColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChipItem(
                    text = "🌐 All Languages",
                    isSelected = tempLanguage == "All",
                    isDark = isDark,
                    onClick = { tempLanguage = "All" }
                )
                languages.take(12).forEach { lang ->
                    val isSelected = tempLanguage.equals(lang.name, ignoreCase = true)
                    FilterChipItem(
                        text = lang.name,
                        isSelected = isSelected,
                        isDark = isDark,
                        onClick = { tempLanguage = lang.name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Quality Filter
            Text(
                text = "Stream Quality",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = sectionHeaderColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "4K", "FHD", "HD").forEach { q ->
                    val isSelected = tempQuality == q
                    FilterChipItem(
                        text = q,
                        isSelected = isSelected,
                        isDark = isDark,
                        onClick = { tempQuality = q }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Actions: Reset & Apply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempCategory = "All"
                        tempCountry = "All"
                        tempLanguage = "All"
                        tempQuality = "All"
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = sectionHeaderColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = sectionHeaderColor
                    )
                }

                Button(
                    onClick = {
                        onApplyFilters(
                            filterOptions.copy(
                                selectedCategory = tempCategory,
                                selectedCountry = tempCountry,
                                selectedLanguage = tempLanguage,
                                selectedQuality = tempQuality
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreamViolet
                    )
                ) {
                    Text(
                        text = "Apply Filters",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val background = if (isSelected) {
        StreamViolet
    } else {
        if (isDark) MidnightCard else Color(0xFFF1F2F8)
    }
    val textColor = if (isSelected) {
        Color.White
    } else {
        if (isDark) TextMuted else TextDarkPrimary
    }
    val borderColor = if (isSelected) StreamGradientStart else if (isDark) MidnightCardBorder else Color(0xFFE2E4EE)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("filter_chip_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = InterFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            color = textColor
        )
    }
}
