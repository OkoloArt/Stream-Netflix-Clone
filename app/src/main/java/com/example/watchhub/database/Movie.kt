package com.example.watchhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity
data class Movie(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val movieName: String,

    @ColumnInfo(name = "image")
    val movieImage: String,

    @ColumnInfo(name = "movieId")
    val movieId: Int,
)