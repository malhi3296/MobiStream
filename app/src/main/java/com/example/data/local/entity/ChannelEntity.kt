package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Channel

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val logoUrl: String,
    val streamUrl: String,
    val category: String,
    val country: String,
    val countryCode: String,
    val language: String,
    val quality: String,
    val isFeatured: Boolean
) {
    fun toDomain(isFavorite: Boolean = false, isPinned: Boolean = false): Channel = Channel(
        id = id,
        name = name,
        logoUrl = logoUrl,
        streamUrl = streamUrl,
        category = category,
        country = country,
        countryCode = countryCode,
        language = language,
        quality = quality,
        isLive = true,
        isFavorite = isFavorite,
        isPinned = isPinned,
        isFeatured = isFeatured
    )


    companion object {
        fun fromDomain(channel: Channel): ChannelEntity = ChannelEntity(
            id = channel.id,
            name = channel.name,
            logoUrl = channel.logoUrl,
            streamUrl = channel.streamUrl,
            category = channel.category,
            country = channel.country,
            countryCode = channel.countryCode,
            language = channel.language,
            quality = channel.quality,
            isFeatured = channel.isFeatured
        )
    }
}

@Entity(tableName = "favorites")
data class FavoriteChannelEntity(
    @PrimaryKey
    val channelId: String,
    val addedAt: Long = System.currentTimeMillis()
)
