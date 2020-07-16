package com.example.android.testprojectar.presentation.markerslibrary

import com.example.android.testprojectar.App
import com.example.android.testprojectar.models.MarkerModel

class MarkerLibraryModel : MarkerLibraryContract.Model {

    private val db = App.markerDao

    override suspend fun getAllMarkers(): MutableList<MarkerModel> {
        return db.getAll()
    }

    override suspend fun addMarker(marker: MarkerModel) {
        db.insert(marker)
    }

    override suspend fun updateMarker(marker: MarkerModel) {
        db.update(marker)
    }

    override suspend fun deleteMarker(marker: MarkerModel) {
        db.delete(marker)
    }
}