package com.example.android.testprojectar.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.testprojectar.models.MarkerModel


@Database(entities = [MarkerModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun markerDao(): MarkerDao
}