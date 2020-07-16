package com.example.android.testprojectar.presentation.ar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.testprojectar.App
import com.example.android.testprojectar.R
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class ARActivity : AppCompatActivity(), Scene.OnUpdateListener {

    lateinit var arFragment: ArFragment
    private var currentMarker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as CustomArFragment
        arFragment.arSceneView.scene.addOnUpdateListener(this@ARActivity)
    }

    fun setupDatabase(config: Config, session: Session?) {
        val imageDatabase = AugmentedImageDatabase(session)

//        config.augmentedImageDatabase = imageDatabase

        GlobalScope.launch {
            for (marker in App.markerDao.getAll()) {
                imageDatabase.addImage(
                    marker.name,
                    loadAugmentedImage(File(marker.imagePath)),
                    0.25f
                )
            }
            //for test
//            imageDatabase.addImage("test", loadAugmentedImage(null), 0.25f)
            config.augmentedImageDatabase = imageDatabase
        }
    }

    private fun loadAugmentedImage(file: File?): Bitmap? {
        try {
            if (file != null) {
                FileInputStream(file).use { inputStream ->
                    return BitmapFactory.decodeStream(
                        inputStream
                    )
                }
            }
            //for test
            else {
                assets.open("billionaire_boys_club.jpeg")
                    .use { inputStream -> return BitmapFactory.decodeStream(inputStream) }
            }
        } catch (e: IOException) {
            Log.e("ImageLoad", "IO Exception", e)
        }

        return null
    }

    override fun onUpdate(frameTime: FrameTime?) {
        val frame = arFragment.arSceneView.arFrame
        val images: Collection<AugmentedImage> =
            frame?.getUpdatedTrackables(AugmentedImage::class.java) ?: mutableListOf()

        for (image in images) {
            Log.d("liza", "onUpdate ${image.trackingState}")
            if (image.trackingState == TrackingState.TRACKING) {
                Log.d("liza", "tracking ${image.name}")

                if (currentMarker != image.name) {
                    val anchor = image.createAnchor(image.centerPose)
                    createModel(anchor, image.name)
                }
            }
        }
    }

    private fun createModel(anchor: Anchor, markerName: String) {
        ViewRenderable.builder()
            .setView(this, R.layout.marker_name)
            .build()
            .thenAccept { viewRenderable -> placeModel(viewRenderable, anchor, markerName) }

    }

    private fun placeModel(viewRendable: ViewRenderable, anchor: Anchor, markerName: String) {
        currentMarker = markerName

        val anchorNode = AnchorNode(anchor)
        anchorNode.renderable = viewRendable

        val transNode = TransformableNode(arFragment.transformationSystem)
        transNode.setParent(anchorNode)
        transNode.renderable = viewRendable
        transNode.select()

        val tvName = viewRendable.view as TextView
        tvName.text = markerName

        arFragment.arSceneView.scene.addChild(anchorNode)
    }
}
