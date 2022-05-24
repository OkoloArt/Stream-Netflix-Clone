package com.example.watchhub.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface MovieDao {

    @Query("SELECT * from movie ORDER BY name ASC")
    fun getItems(): Flow<List<Movie>>

    @Query("SELECT * from movie WHERE id = :id")
    fun getItem(id: Int): Flow<Movie>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("DELETE FROM movie WHERE name = :name")
    suspend fun deleteFavouriteMovie(name: String)

    @Query("SELECT * FROM movie WHERE name = :name AND movieId = :movieId ")
    suspend fun getUser(name: String, movieId:Int): Movie?
}