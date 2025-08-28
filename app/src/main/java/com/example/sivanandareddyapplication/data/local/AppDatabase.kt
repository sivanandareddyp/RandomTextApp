package com.example.sivanandareddyapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RandomTextEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomTextDao(): RandomTextDao

    companion object {
        fun build(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "random_text.db"
        ).build()
    }
}
