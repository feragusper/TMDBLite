package com.feragusper.tmdblite.catalog.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feragusper.tmdblite.catalog.model.TVShow

@Entity(tableName = "tv_shows")
data class TVShowDBEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "poster_url") val posterUrl: String,
    @ColumnInfo(name = "backdrop_url") val backdropUrl: String,
    val name: String,
    val overview: String,
    @ColumnInfo(name = "first_air_date") val firstAirDate: String?,
) {
    fun toTVShow() = TVShow(
        id = id,
        posterUrl = posterUrl,
        name = name,
        overview = overview,
        firstAirDate = firstAirDate,
        backDropUrl = backdropUrl
    )

    companion object {
        fun from(tvShow: TVShow): TVShowDBEntity {
            return TVShowDBEntity(
                id = tvShow.id,
                posterUrl = tvShow.posterUrl,
                name = tvShow.name,
                overview = tvShow.overview,
                firstAirDate = tvShow.firstAirDate,
                backdropUrl = tvShow.backDropUrl
            )
        }
    }
}