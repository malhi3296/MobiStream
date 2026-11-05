package com.example.data.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String = "",
    val streamUrl: String,
    val category: String = "General",
    val country: String = "International",
    val countryCode: String = "INT",
    val language: String = "English",
    val quality: String = "FHD",
    val isLive: Boolean = true,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val isFeatured: Boolean = false
)


data class Category(
    val id: String,
    val name: String,
    val iconName: String = "",
    val channelCount: Int = 0
)

data class Country(
    val code: String,
    val name: String,
    val flag: String = ""
)

data class Language(
    val code: String,
    val name: String
)
