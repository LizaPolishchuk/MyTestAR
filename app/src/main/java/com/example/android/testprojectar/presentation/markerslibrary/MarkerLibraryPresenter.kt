package com.example.android.testprojectar.presentation.markerslibrary

import com.example.android.testprojectar.models.MarkerModel
import kotlinx.coroutines.*

class MarkerLibraryPresenter(private var view: MarkerLibraryContract.View?) :
    MarkerLibraryContract.Presenter {

    private val model: MarkerLibraryContract.Model = MarkerLibraryModel()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun loadAllMarkers() {
        view?.onAllMarkersLoaded(withContext(Dispatchers.IO) {
            model.getAllMarkers()
        })
    }

    override fun addMarker(marker: MarkerModel) {
        coroutineScope.launch {
            model.addMarker(marker)
        }
    }

    override fun updateMarker(marker: MarkerModel) {
        coroutineScope.launch {
            model.updateMarker(marker)
        }
    }

    override fun deleteMarker(marker: MarkerModel) {
        coroutineScope.launch {
            model.deleteMarker(marker)
        }
    }

    override fun onDestroy() {
        view = null
        coroutineScope.cancel()
    }
}