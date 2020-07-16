package com.example.android.testprojectar.listeners

import com.example.android.testprojectar.models.MarkerModel

interface OnMarkerClickListener {
    fun clickOnEditMarker(marker: MarkerModel, position: Int)
    fun clickOnRemoveMarker(marker: MarkerModel, position: Int)
}