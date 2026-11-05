package com.example.data.remote

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface IptvApiService {

    @GET
    suspend fun fetchPlaylist(@Url url: String): ResponseBody

    companion object {
        private const val BASE_URL = "https://iptv-org.github.io/"

        fun create(): IptvApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
                .create(IptvApiService::class.java)
        }
    }
}
