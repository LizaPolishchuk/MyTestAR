package com.example.android.testprojectar.presentation.markerslibrary

import com.example.android.testprojectar.models.MarkerModel

interface MarkerLibraryContract {

    interface Model {
        suspend fun getAllMarkers(): MutableList<MarkerModel>
        suspend fun addMarker(marker: MarkerModel)
        suspend fun updateMarker(marker: MarkerModel)
        suspend fun deleteMarker(marker: MarkerModel)
    }

    interface View {
        fun onAllMarkersLoaded(markersList: MutableList<MarkerModel>)
    }

    interface Presenter {
        suspend fun loadAllMarkers()
        fun addMarker(marker: MarkerModel)
        fun deleteMarker(marker: MarkerModel)
        fun updateMarker(marker: MarkerModel)
        fun onDestroy()
    }
}