package com.example.domain.backup

import org.json.JSONArray
import org.json.JSONObject

data class MobiStreamBackup(
    val version: String = "1.0.3",
    val timestamp: Long = System.currentTimeMillis(),
    val favoriteChannelIds: List<String> = emptyList(),
    val pinnedChannelIds: List<String> = emptyList(),
    val preferredLanguages: List<String> = emptyList(),
    val favoriteCategories: List<String> = emptyList()
)

object BackupManager {

    fun createBackupJson(
        favoriteChannelIds: List<String>,
        pinnedChannelIds: List<String>,
        preferredLanguages: List<String>,
        favoriteCategories: List<String>
    ): String {
        val root = JSONObject()
        root.put("app", "MobiStream")
        root.put("version", "1.0.3")
        root.put("timestamp", System.currentTimeMillis())

        val favArray = JSONArray()
        favoriteChannelIds.forEach { favArray.put(it) }
        root.put("favorites", favArray)

        val pinnedArray = JSONArray()
        pinnedChannelIds.forEach { pinnedArray.put(it) }
        root.put("pinned", pinnedArray)

        val langArray = JSONArray()
        preferredLanguages.forEach { langArray.put(it) }
        root.put("preferredLanguages", langArray)

        val catArray = JSONArray()
        favoriteCategories.forEach { catArray.put(it) }
        root.put("favoriteCategories", catArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonStr: String): MobiStreamBackup? {
        return try {
            val root = JSONObject(jsonStr)
            val version = root.optString("version", "1.0.0")
            val timestamp = root.optLong("timestamp", System.currentTimeMillis())

            val favorites = mutableListOf<String>()
            val favArray = root.optJSONArray("favorites")
            if (favArray != null) {
                for (i in 0 until favArray.length()) {
                    favorites.add(favArray.getString(i))
                }
            }

            val pinned = mutableListOf<String>()
            val pinnedArray = root.optJSONArray("pinned")
            if (pinnedArray != null) {
                for (i in 0 until pinnedArray.length()) {
                    pinned.add(pinnedArray.getString(i))
                }
            }

            val languages = mutableListOf<String>()
            val langArray = root.optJSONArray("preferredLanguages")
            if (langArray != null) {
                for (i in 0 until langArray.length()) {
                    languages.add(langArray.getString(i))
                }
            }

            val categories = mutableListOf<String>()
            val catArray = root.optJSONArray("favoriteCategories")
            if (catArray != null) {
                for (i in 0 until catArray.length()) {
                    categories.add(catArray.getString(i))
                }
            }

            MobiStreamBackup(
                version = version,
                timestamp = timestamp,
                favoriteChannelIds = favorites,
                pinnedChannelIds = pinned,
                preferredLanguages = languages,
                favoriteCategories = categories
            )
        } catch (e: Exception) {
            null
        }
    }
}
