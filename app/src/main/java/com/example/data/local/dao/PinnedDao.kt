package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PinnedChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnedDao {
    @Query("SELECT channelId FROM pinned_channels ORDER BY pinnedAt DESC")
    fun getPinnedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPinned(pinned: PinnedChannelEntity)

    @Query("DELETE FROM pinned_channels WHERE channelId = :channelId")
    suspend fun deletePinned(channelId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM pinned_channels WHERE channelId = :channelId)")
    suspend fun isPinned(channelId: String): Boolean
}
