package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.FavoriteChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY name ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE category = :category ORDER BY name ASC")
    fun getChannelsByCategory(category: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isFeatured = 1")
    fun getFeaturedChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id IN (SELECT channelId FROM favorites) ORDER BY name ASC")
    fun getFavoriteChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id IN (SELECT channelId FROM pinned_channels) ORDER BY name ASC")
    fun getPinnedChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchChannels(query: String): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("SELECT COUNT(*) FROM channels")
    suspend fun getCount(): Int

    @Query("DELETE FROM channels")
    suspend fun clearAll()
}

@Dao
interface FavoriteDao {
    @Query("SELECT channelId FROM favorites ORDER BY addedAt DESC")
    fun getFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteChannelEntity)

    @Query("DELETE FROM favorites WHERE channelId = :channelId")
    suspend fun deleteFavorite(channelId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE channelId = :channelId)")
    suspend fun isFavorite(channelId: String): Boolean
}
