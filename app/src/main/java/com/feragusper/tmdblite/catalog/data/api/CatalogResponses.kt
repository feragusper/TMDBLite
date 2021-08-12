package com.feragusper.tmdblite.catalog.data.api

import com.feragusper.tmdblite.BuildConfig
import com.feragusper.tmdblite.catalog.model.Genre
import com.feragusper.tmdblite.catalog.model.TVShow
import com.feragusper.tmdblite.catalog.model.TVShowId
import com.google.gson.annotations.SerializedName

/**
 * Data class that represents a movie list response from TMDB.
 *
 * Not all of the fields returned from the API are represented here; only the ones used in this
 * project are listed below. For a full list of fields, consult the API documentation
 * [here](https://developers.themoviedb.org/3/tv/get-popular-tv-shows).
 */

data class TVShowGenreListResponse(
    val genres: List<GenreEntity>
) {
    fun toGenreList() = genres.map { genreEntity -> genreEntity.toGenre() }
}

data class GenreEntity(
    val id: Int,
    val name: String
) {
    fun toGenre() = Genre(
        id = id,
        name = name
    )
}

interface TVShowListPageable {
    val totalPages: Int
    fun toTVShows(genreList: List<Genre>): List<TVShow>
}

data class TVShowListResponse(
    val results: List<TVShowEntity>,
    val page: Int,
    @field: SerializedName("total_pages") override val totalPages: Int
) : TVShowListPageable {
    override fun toTVShows(genreList: List<Genre>) = results.map { tvShowEntity -> tvShowEntity.toTVShow(genreList) }
}

data class TVShowEntity(
    private val id: TVShowId,
    @field: SerializedName("poster_path") private val posterPath: String,
    @field: SerializedName("backdrop_path") private val backdropPath: String,
    private val name: String,
    private val overview: String,
    @field: SerializedName("first_air_date") private val firstAirDate: String,
    @field: SerializedName("genre_ids") private val genreIds: List<Int>,
) {

    fun toTVShow(genreList: List<Genre>) = TVShow(
        id = id,
        posterUrl = BuildConfig.TMDB_API_HOST_IMAGES + posterPath,
        backDropUrl = BuildConfig.TMDB_API_HOST_IMAGES + backdropPath,
        name = name,
        overview = overview,
        firstAirDate = firstAirDate,
        genres = genreList.filter { genre ->
            genreIds.contains(genre.id)
        }
    )
}
