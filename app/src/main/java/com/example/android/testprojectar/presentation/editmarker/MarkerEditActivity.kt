package com.example.android.testprojectar.presentation.editmarker

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.android.testprojectar.App
import com.example.android.testprojectar.R
import com.example.android.testprojectar.models.MarkerModel
import com.example.android.testprojectar.presentation.markerslibrary.RC_ADD_MARKER
import com.example.android.testprojectar.presentation.markerslibrary.RC_EDIT_MARKER
import com.example.android.testprojectar.utils.FileUtils
import kotlinx.android.synthetic.main.activity_marker_edit.*
import java.io.File
import java.io.IOException


const val IMAGE_URI_EXTRA = "imageUri"
const val MARKER_EXTRA = "marker"
const val MARKER_POSITION_EXTRA = "markerPosition"

class MarkerEditActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var marker: MarkerModel? = null
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_edit)

        intent?.extras?.let {
            imageUri = it.get(IMAGE_URI_EXTRA) as Uri?
            position = it.get(MARKER_POSITION_EXTRA) as Int?
            marker = it.get(MARKER_EXTRA) as MarkerModel?

        }

        var file: File? = null
        marker?.let {
            file = File(it.imagePath)
            etMarkerName.setText(it.name)
        }

        Glide.with(this)
            .load(imageUri ?: file)
            .into(ivMarkerPreview)
    }

    private fun saveImage(uri: Uri?) {
        if (uri == null) {
            Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show()
            finish()

        }
        Glide.with(this).asBitmap().apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.IMMEDIATE)
        )
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?, model: Any,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(
                        this@MarkerEditActivity,
                        getString(R.string.please_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    vgLoading.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap, model: Any, target: Target<Bitmap>,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    try {
                        val image = FileUtils.prepareImageFile(this@MarkerEditActivity, resource)
                        addMarkerToDB(image)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    return false
                }
            })
            .load(uri)
            .submit()
    }


    fun addMarkerToDB(imageFile: File) {
        try {
            val name = etMarkerName.text.toString()
            val markerModel = MarkerModel(name, imageFile.path)
            finishWithResult(markerModel)
        } catch (e: IOException) {
            Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show()
            Log.e(MarkerEditActivity::class.java.name, "IO Exception", e)
        }
    }

    private fun finishWithResult(markerModel: MarkerModel) {
        val data = Intent()
        data.putExtra(MARKER_EXTRA, markerModel)
        data.putExtra(MARKER_POSITION_EXTRA, position)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save_marker, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemSave) {
            if (etMarkerName.text.isNotEmpty()) {
                hideKeyboard()
                vgLoading.visibility = View.VISIBLE

                if (imageUri != null) {
                    saveImage(imageUri)
                } else {
                    marker?.let {
                        it.name = etMarkerName.text.toString()
                        finishWithResult(it)
                    }
                }
            } else {
                etMarkerName.error = getString(R.string.enter_the_name_please)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}