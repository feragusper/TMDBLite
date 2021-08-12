package com.feragusper.tmdblite.catalog.data.api

import com.feragusper.tmdblite.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Used to connect to the TMDB API to query catalog
 */
interface CatalogService {

    @GET("genre/tv/list")
    suspend fun getTVShowGenres(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): TVShowGenreListResponse

    @GET("search/tv")
    suspend fun searchTVShows(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int,
    ): TVShowListResponse

    @GET("tv/popular")
    suspend fun getPopularTVShows(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("page") page: Int,
    ): TVShowListResponse

    companion object {
        fun create(): CatalogService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BuildConfig.TMDB_API_HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CatalogService::class.java)
        }
    }
}
