package com.example.android.testprojectar

import com.example.android.testprojectar.database.AppDatabase
import androidx.room.Room
import android.app.Application
import com.example.android.testprojectar.database.MarkerDao


class App : Application() {


    override fun onCreate() {
        super.onCreate()
        instance = this

        val database = Room.databaseBuilder(this, AppDatabase::class.java, "database")
            .build()
        markerDao = database.markerDao()
    }

    companion object {
        lateinit var instance: App
        lateinit var markerDao: MarkerDao
    }
}