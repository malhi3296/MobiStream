package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.data.model.Country
import com.example.data.model.Language
import com.example.domain.FilterOptions
import com.example.ui.theme.MobiStreamTheme

@OptIn(ExperimentalMaterial3Api::class)
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
    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = modifier.testTag("filter_bottom_sheet")
    ) {
        FilterBottomSheetContent(
            filterOptions = filterOptions,
            categories = categories,
            countries = countries,
            languages = languages,
            onApplyFilters = { updated ->
                onApplyFilters(updated)
                onDismiss()
            },
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(
    filterOptions: FilterOptions,
    categories: List<Category>,
    countries: List<Country>,
    languages: List<Language>,
    onApplyFilters: (FilterOptions) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    var tempCategory by remember(filterOptions) { mutableStateOf(filterOptions.selectedCategory) }
    var tempCountry by remember(filterOptions) { mutableStateOf(filterOptions.selectedCountry) }
    var tempLanguage by remember(filterOptions) { mutableStateOf(filterOptions.selectedLanguage) }
    var tempQuality by remember(filterOptions) { mutableStateOf(filterOptions.selectedQuality) }

    val defaultCategories = remember(categories) {
        val dynamic = categories.map { it.name.trim() }.filter { it.isNotBlank() }
        val fallback = listOf("Sports", "News", "Movies", "Business", "Comedy", "Music", "Kids", "Documentary")
        listOf("All") + (dynamic.ifEmpty { fallback }).distinct().take(12)
    }

    val defaultCountries = remember(countries) {
        val base = listOf(
            "US" to "United States",
            "IN" to "India",
            "GB" to "United Kingdom",
            "QA" to "Qatar",
            "CA" to "Canada",
            "AU" to "Australia",
            "DE" to "Germany",
            "FR" to "France"
        )
        if (countries.isEmpty()) base
        else countries.take(10).map { it.code to it.name }.ifEmpty { base }
    }

    val defaultLanguages = remember(languages) {
        val fallback = listOf(
            "English", "Hindi", "Punjabi", "Urdu", "Tamil",
            "Spanish", "Arabic", "French", "German", "Russian"
        )
        val dynamic = languages.map { it.name.trim() }.filter { it.isNotBlank() }
        listOf("All") + (dynamic.ifEmpty { fallback }).distinct().take(12)
    }

    val qualities = remember { listOf("All", "FHD", "HD", "SD") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Apply Filters",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onSurface
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleSmall,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            defaultCategories.forEach { cat ->
                val isSelected = tempCategory.equals(cat, ignoreCase = true)
                MobiChip(
                    label = cat,
                    selected = isSelected,
                    onClick = { tempCategory = cat }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Countries",
            style = MaterialTheme.typography.titleSmall,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MobiChip(
                label = "All",
                selected = tempCountry.equals("All", ignoreCase = true),
                onClick = { tempCountry = "All" }
            )
            defaultCountries.forEach { (cc, name) ->
                val isSelected = tempCountry.equals(cc, ignoreCase = true)
                MobiChip(
                    label = name,
                    leadingGlyph = getCountryFlag(cc),
                    selected = isSelected,
                    onClick = { tempCountry = if (isSelected) "All" else cc }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Languages",
            style = MaterialTheme.typography.titleSmall,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            defaultLanguages.forEach { lang ->
                val isSelected = tempLanguage.equals(lang, ignoreCase = true)
                MobiChip(
                    label = lang,
                    leadingGlyph = if (lang.equals("All", ignoreCase = true)) null else getLanguageGlyph(lang),
                    selected = isSelected,
                    onClick = { tempLanguage = lang }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Quality",
            style = MaterialTheme.typography.titleSmall,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            qualities.forEach { q ->
                val isSelected = tempQuality.equals(q, ignoreCase = true)
                MobiChip(
                    label = q,
                    selected = isSelected,
                    onClick = { tempQuality = q }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MobiButton(
                text = "Clear",
                style = MobiButtonStyle.TONAL,
                onClick = {
                    tempCategory = "All"
                    tempCountry = "All"
                    tempLanguage = "All"
                    tempQuality = "All"
                    onApplyFilters(
                        filterOptions.copy(
                            selectedCategory = "All",
                            selectedCountry = "All",
                            selectedLanguage = "All",
                            selectedQuality = "All"
                        )
                    )
                },
                modifier = Modifier.weight(0.38f)
            )

            MobiButton(
                text = "Apply Filters",
                style = MobiButtonStyle.PRIMARY,
                onClick = {
                    onApplyFilters(
                        filterOptions.copy(
                            selectedCategory = tempCategory,
                            selectedCountry = tempCountry,
                            selectedLanguage = tempLanguage,
                            selectedQuality = tempQuality
                        )
                    )
                },
                modifier = Modifier
                    .weight(0.62f)
                    .testTag("apply_filters_button")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FilterBottomSheetPreview() {
    MobiStreamTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FilterBottomSheetContent(
                filterOptions = FilterOptions(selectedCategory = "News", selectedCountry = "IN"),
                categories = emptyList(),
                countries = emptyList(),
                languages = emptyList(),
                onApplyFilters = {},
                onDismiss = {}
            )
        }
    }
}
