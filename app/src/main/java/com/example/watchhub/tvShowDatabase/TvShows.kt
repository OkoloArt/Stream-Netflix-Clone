package com.example.watchhub.tvShowDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TvShows(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val tvShowName: String,

    @ColumnInfo(name = "image")
    val tvShowImage: String,

    @ColumnInfo(name = "tvId")
    val tvShowId: Int,
)