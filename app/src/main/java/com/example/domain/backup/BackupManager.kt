package com.example.domain.backup

import org.json.JSONArray
import org.json.JSONObject

data class MobiStreamBackup(
    val version: String = "1.0.4",
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
        root.put("version", "1.0.4")
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

    /**
     * Requirement: Make backup on device automatically once everyday.
     * Checks if 24 hours have elapsed since the last automatic backup,
     * and if so, writes an automatic snapshot to mobistream_daily_autobackup.json.
     */
    fun performDailyAutoBackupIfDue(
        context: android.content.Context,
        favoriteChannelIds: List<String>,
        pinnedChannelIds: List<String>,
        preferredLanguages: List<String>,
        favoriteCategories: List<String>
    ): Boolean {
        return try {
            val prefs = context.getSharedPreferences("mobistream_auto_backup", android.content.Context.MODE_PRIVATE)
            val lastBackupTime = prefs.getLong("last_auto_backup_timestamp", 0L)
            val now = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L

            if (now - lastBackupTime >= oneDayMillis) {
                val json = createBackupJson(
                    favoriteChannelIds,
                    pinnedChannelIds,
                    preferredLanguages,
                    favoriteCategories
                )
                val backupFile = java.io.File(context.filesDir, "mobistream_daily_autobackup.json")
                backupFile.writeText(json)
                prefs.edit().putLong("last_auto_backup_timestamp", now).apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getLastAutoBackupTimestamp(context: android.content.Context): Long {
        val prefs = context.getSharedPreferences("mobistream_auto_backup", android.content.Context.MODE_PRIVATE)
        return prefs.getLong("last_auto_backup_timestamp", 0L)
    }
}
