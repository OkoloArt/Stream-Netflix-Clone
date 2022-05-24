package com.example.watchhub.tvShowDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.watchhub.database.MovieDatabase


@Database(entities = [TvShows::class], version = 1, exportSchema = false)
abstract class TvDataBase: RoomDatabase() {

    abstract fun testDao(): TvShowDao

    companion object {
        @Volatile
        private var INSTANCE: TvDataBase? = null

        fun getDatabase(context: Context): TvDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TvDataBase::class.java,
                    "tv_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}