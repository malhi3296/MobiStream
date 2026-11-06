package com.example.domain

enum class GroupType {
    CATEGORY,
    COUNTRY,
    LANGUAGE
}

data class SelectedGroup(
    val type: GroupType,
    val title: String,
    val identifier: String
)
