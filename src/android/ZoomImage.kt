package de.impacgroup.zoomimageview.module

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity

interface ZoomImage {
    fun present(activity: AppCompatActivity, bitmap: Bitmap, point: ImageRect?)

    fun present(activity: AppCompatActivity, bitmap: Bitmap, point: ImageRect?, closeButton: Boolean)
}