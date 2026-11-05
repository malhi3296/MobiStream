package com.example.domain

data class FilterOptions(
    val selectedCategory: String = "All",
    val selectedCountry: String = "All",
    val selectedLanguage: String = "All",
    val selectedQuality: String = "All",
    val searchQuery: String = ""
) {
    val hasActiveFilters: Boolean
        get() = selectedCategory != "All" ||
                selectedCountry != "All" ||
                selectedLanguage != "All" ||
                selectedQuality != "All" ||
                searchQuery.isNotBlank()
}
