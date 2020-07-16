package com.example.android.testprojectar.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright     2020 (C) < напиши тут компанию>
 *
 * Author      : Liza Polishchuk
 * Package     : com.example.android.testprojectar
 * Class       : FileUtils
 * Created on  : 04.03.2020
 * Time        : 22:40
 */


const val IMAGE_DIR_APP = "AR images"
const val TAKE_PHOTO_EXTRA = "return-data"
class FileUtils {

    companion object {

        fun prepareImageFile(context: Context, bitmap: Bitmap): File {
            val copyBitmap = bitmap.copy(bitmap.config, true)
            val fileName = System.currentTimeMillis().toString() + "_" + "ar.jpg"

            if (File(context.getExternalFilesDir(null), fileName).exists()) {
                File(context.getExternalFilesDir(null), fileName).delete()
            }

            return compress(
                context,
                copyBitmap,
                fileName,
                true
            )
        }

        private fun compress(context: Context, bitmap: Bitmap, fileName: String, firstCompress: Boolean): File {
            var file = File(context.getExternalFilesDir(null), fileName)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,
                getQuality(file), bos)
            val bitmapdata = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

            if (firstCompress && file.length() / 1024 / 1024 > 0) {
                file = compress(
                    context,
                    bitmap,
                    fileName,
                    false
                )
            }

            return file
        }

        private fun getQuality(file: File): Int {
            return if (file.length() / 1024 / 1024 > 0) 70 else 90
        }


        fun getOutputImageFileUri(context: Context): Uri? {
            val file = getOutputMediaFile(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            ) ?: return null
            return FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
        }

        private fun getOutputMediaFile(mediaType: Int): File? {
            val externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val appMediaDir = File(externalStorage,
                IMAGE_DIR_APP
            )

            if (!appMediaDir.exists()) {
                if (!appMediaDir.mkdirs()) {
                    Log.d(FileUtils::class.java.name, "Failed create $IMAGE_DIR_APP directory")
                    return null
                }
            }

            val mediaStorageDir = File(appMediaDir, "ar_markers")
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(FileUtils::class.java.name, "Failed create 'ar_markers' directory")
                    return null
                }
            }

            val mediaStorageDirPath = mediaStorageDir.path
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())

            var mediaFile: File? = null
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                mediaFile = File(mediaStorageDirPath, "IMG_$timeStamp.jpg")
            }
            return mediaFile
        }
    }
}