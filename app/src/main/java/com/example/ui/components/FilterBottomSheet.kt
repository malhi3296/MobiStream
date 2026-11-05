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
import com.example.ui.theme.StreamGradientEnd
import com.example.ui.theme.StreamGradientStart
import com.example.ui.theme.StreamPrimaryGradient
import com.example.ui.theme.StreamViolet
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

    var tempCategory by remember { mutableStateOf(filterOptions.selectedCategory) }
    var tempCountry by remember { mutableStateOf(filterOptions.selectedCountry) }
    var tempLanguage by remember { mutableStateOf(filterOptions.selectedLanguage) }
    var tempQuality by remember { mutableStateOf(filterOptions.selectedQuality) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MidnightBackground,
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
                        tint = GlowLilac,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Filter Channels",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextWhite
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Categories Filter
            Text(
                text = "Genre / Category",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = GlowLilac
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
                        onClick = { tempCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Country Filter
            Text(
                text = "Country / Region",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = GlowLilac
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
                    onClick = { tempCountry = "All" }
                )
                countries.take(16).forEach { country ->
                    val isSelected = tempCountry.equals(country.code, ignoreCase = true) ||
                            tempCountry.equals(country.name, ignoreCase = true)
                    FilterChipItem(
                        text = "${country.flag} ${country.name}",
                        isSelected = isSelected,
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
                color = GlowLilac
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val allLangs = listOf("All") + languages.map { it.name }.distinct()
                allLangs.take(10).forEach { lang ->
                    val isSelected = tempLanguage.equals(lang, ignoreCase = true)
                    FilterChipItem(
                        text = lang,
                        isSelected = isSelected,
                        onClick = { tempLanguage = lang }
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
                color = GlowLilac
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "4K", "FHD", "HD").forEach { q ->
                    val isSelected = tempQuality.equals(q, ignoreCase = true)
                    FilterChipItem(
                        text = q,
                        isSelected = isSelected,
                        onClick = { tempQuality = q }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
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
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextMuted
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("reset_filters_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
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
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreamViolet
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .testTag("apply_filters_button")
                ) {
                    Text(
                        text = "Apply Filters",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (isSelected) StreamViolet else MidnightCard)
            .border(
                width = 1.dp,
                color = if (isSelected) StreamGradientStart else MidnightCardBorder,
                shape = shape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = InterFontFamily,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else TextMuted
        )
    }
}
