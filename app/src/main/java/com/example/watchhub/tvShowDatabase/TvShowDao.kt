package com.example.watchhub.tvShowDatabase

import androidx.room.*
import com.example.watchhub.database.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowDao {

    @Query("SELECT * from tvshows ORDER BY name ASC")
    fun getItems(): Flow<List<TvShows>>

    @Query("SELECT * from tvshows WHERE id = :id")
    fun getItem(id: Int): Flow<TvShows>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tvShows: TvShows)

    @Update
    suspend fun update(tvShows: TvShows)

    @Delete
    suspend fun delete(tvShows: TvShows)

    @Query("DELETE FROM tvshows WHERE name = :name")
    suspend fun deleteFavouriteMovie(name: String)

    @Query("SELECT * FROM tvshows WHERE name = :name AND tvId = :movieId ")
    suspend fun getUser(name: String, movieId:Int): TvShows?
}