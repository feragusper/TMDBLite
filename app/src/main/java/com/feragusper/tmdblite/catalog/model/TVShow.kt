package com.feragusper.tmdblite.catalog.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

typealias TVShowId = Int

@Parcelize
data class TVShow(
    val id: TVShowId,
    val posterUrl: String,
    val backDropUrl: String,
    val name: String,
    val overview: String,
    val firstAirDate: String?,
    var favorite: Boolean = false,
    val latest: Boolean = false,
    val genres: List<Genre>? = null
) : Parcelable {

    @IgnoredOnParcel
    val genre = genres?.joinToString(
        separator = " | "
    ) { genre ->
        genre.name
    }

    @IgnoredOnParcel
    val year: String
        get() {
            return if (firstAirDate.isNullOrEmpty()) {
                ""
            } else {
                firstAirDate.subSequence(0, 4).toString()
            }
        }
}