package de.impacgroup.zoomimageview.module

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity

interface ZoomImage {
    fun present(activity: AppCompatActivity, bitmap: Bitmap, point: ImagePosition?)

    fun present(activity: AppCompatActivity, bitmap: Bitmap, point: ImagePosition?, closeButton: Boolean)
}