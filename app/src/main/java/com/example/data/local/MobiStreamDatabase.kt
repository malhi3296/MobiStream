package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChannelDao
import com.example.data.local.dao.FavoriteDao
import com.example.data.local.entity.ChannelEntity
import com.example.data.local.entity.FavoriteChannelEntity

@Database(
    entities = [ChannelEntity::class, FavoriteChannelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MobiStreamDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: MobiStreamDatabase? = null

        fun getInstance(context: Context): MobiStreamDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MobiStreamDatabase::class.java,
                    "mobistream_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
