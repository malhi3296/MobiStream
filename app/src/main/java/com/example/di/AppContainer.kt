package com.example.di

import android.content.Context
import com.example.data.local.MobiStreamDatabase
import com.example.data.remote.IptvApiService
import com.example.data.repository.ChannelRepository

interface AppContainer {
    val channelRepository: ChannelRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: MobiStreamDatabase by lazy {
        MobiStreamDatabase.getInstance(context)
    }

    private val apiService: IptvApiService by lazy {
        IptvApiService.create()
    }

    override val channelRepository: ChannelRepository by lazy {
        ChannelRepository(
            channelDao = database.channelDao(),
            favoriteDao = database.favoriteDao(),
            apiService = apiService
        )
    }
}
