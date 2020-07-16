package com.example.android.testprojectar.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.io.Serializable

@Entity
data class MarkerModel (var name: String,
                         val imagePath: String
) : Serializable{
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}