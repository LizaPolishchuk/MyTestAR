package com.example.android.testprojectar.database

import androidx.room.*
import com.example.android.testprojectar.models.MarkerModel


@Dao
interface MarkerDao {

    @Query("SELECT * FROM markermodel")
    fun getAll(): MutableList<MarkerModel>

    @Insert
    fun insert(marker: MarkerModel)

    @Update
    fun update(marker: MarkerModel)

    @Delete
    fun delete(marker: MarkerModel)

}