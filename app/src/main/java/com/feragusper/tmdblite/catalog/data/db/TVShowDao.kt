package com.feragusper.tmdblite.catalog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.feragusper.tmdblite.catalog.model.TVShowId
import kotlinx.coroutines.flow.Flow

@Dao
interface TVShowDao {
    @Query("SELECT * FROM tv_shows ORDER BY name")
    fun getTVShows(): Flow<List<TVShowDBEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tvShow: TVShowDBEntity)

    @Query("DELETE FROM tv_shows WHERE id = :id")
    suspend fun delete(id: TVShowId)

    @Query("SELECT EXISTS(SELECT 1 FROM tv_shows WHERE id = :id LIMIT 1)")
    fun isSubscribed(id: TVShowId): Flow<Boolean>
}
