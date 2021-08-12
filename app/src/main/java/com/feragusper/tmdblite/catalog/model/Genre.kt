package com.feragusper.tmdblite.catalog.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias GenreId = Int

@Parcelize
data class Genre(
    val id: GenreId,
    val name: String,
) : Parcelable