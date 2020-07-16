package com.example.android.testprojectar.presentation.markerslibrary

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.testprojectar.App
import com.example.android.testprojectar.R
import com.example.android.testprojectar.listeners.OnMarkerClickListener
import com.example.android.testprojectar.models.MarkerModel
import com.example.android.testprojectar.presentation.editmarker.IMAGE_URI_EXTRA
import com.example.android.testprojectar.presentation.editmarker.MARKER_EXTRA
import com.example.android.testprojectar.presentation.editmarker.MARKER_POSITION_EXTRA
import com.example.android.testprojectar.presentation.editmarker.MarkerEditActivity
import com.example.android.testprojectar.utils.FileUtils.Companion.getOutputImageFileUri
import com.example.android.testprojectar.utils.TAKE_PHOTO_EXTRA
import kotlinx.android.synthetic.main.activity_markers_library.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


const val RC_PICK_FROM_GALLERY = 1
const val RC_PICK_FROM_CAMERA = 2
const val RC_ADD_MARKER = 3
const val RC_EDIT_MARKER = 4

class MarkersLibraryActivity : AppCompatActivity(),
    OnMarkerClickListener, MarkerLibraryContract.View {
    var imageOutputFileUri: Uri? = null
    var markersList: MutableList<MarkerModel> = mutableListOf()
    private lateinit var markersAdapter: MarkersAdapter
    private val presenter: MarkerLibraryPresenter by lazy {
        MarkerLibraryPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markers_library)

        btnAddImage.setOnClickListener { showImagePickerMenu() }

        CoroutineScope(Dispatchers.IO).launch {
            presenter.loadAllMarkers()
        }
    }

    override fun onAllMarkersLoaded(markersList: MutableList<MarkerModel>) {
        this.markersList = markersList
        tvNoMarkers.visibility = if (markersList.size == 0) View.VISIBLE else View.GONE
        markersAdapter = MarkersAdapter(markersList,this)

        rvMarkersList.layoutManager = LinearLayoutManager(this)
        rvMarkersList.adapter = markersAdapter
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_PICK_FROM_CAMERA -> run {
                    if (data == null || data.data == null) {
                        return
                    }

                    openAddMarkerActivity(data.data)
                }
                RC_PICK_FROM_GALLERY -> run {
                    try {
                        imageOutputFileUri?.let { openAddMarkerActivity(it) }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                RC_ADD_MARKER -> run {
                    val newMarker = data?.extras?.getSerializable(MARKER_EXTRA) as MarkerModel?
                    newMarker?.let {
                        presenter.addMarker(it)
                        markersList.add(0, it)
                        markersAdapter.notifyItemInserted(0)

                        if (tvNoMarkers.visibility == View.VISIBLE) tvNoMarkers.visibility = View.GONE
                    }
                }
                RC_EDIT_MARKER -> run {
                    val marker = data?.extras?.getSerializable(MARKER_EXTRA) as MarkerModel?
                    val positon = data?.extras?.getInt(MARKER_POSITION_EXTRA)
                    positon?.also {
                        marker?.let {
                            presenter.updateMarker(it)
                            markersList[positon] = it
                            markersAdapter.notifyItemChanged(positon)
                        }
                    }
                }
            }
        }
    }

    private fun openAddMarkerActivity(uri: Uri?) {
        val intent = Intent(this, MarkerEditActivity::class.java)
        intent.putExtra(IMAGE_URI_EXTRA, uri)
        startActivityForResult(intent, RC_ADD_MARKER)
    }

    private fun showImagePickerMenu() {
        val popup = PopupMenu(this, btnAddImage)
        popup.menuInflater.inflate(R.menu.menu_profile_choose_picture, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            if (item.title === getString(R.string.attach_from_gallery)) {
                startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RC_PICK_FROM_CAMERA)
            } else {
                imageOutputFileUri = getOutputImageFileUri(this)
                if (imageOutputFileUri != null) {
                    startActivityForResult(
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            .putExtra(MediaStore.EXTRA_OUTPUT, imageOutputFileUri)
                            .putExtra(TAKE_PHOTO_EXTRA, true),
                        RC_PICK_FROM_GALLERY
                    )
                }
            }
            true
        }
        popup.show()
    }

    override fun clickOnEditMarker(marker: MarkerModel, position: Int) {
        val intent = Intent(this, MarkerEditActivity::class.java)
        intent.putExtra(MARKER_EXTRA, marker)
        intent.putExtra(MARKER_POSITION_EXTRA, position)
        startActivityForResult(intent, RC_EDIT_MARKER)
    }

    override fun clickOnRemoveMarker(marker: MarkerModel, position: Int) {
        position.let {
            presenter.deleteMarker(marker)
            markersList.removeAt(it)
            markersAdapter.notifyItemRemoved(it)

            if (markersList.size == 0) tvNoMarkers.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}