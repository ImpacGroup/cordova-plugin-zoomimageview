package de.impacgroup.zoomimageview

import com.google.gson.annotations.SerializedName
import de.impacgroup.zoomimageview.module.ImageRect

open class ImageInfo(

    @SerializedName("image")
    val image: String,
    @SerializedName("closeButton")
    val closeButton: Boolean,
    @SerializedName("imageRect")
    val imageRect: ImageRect?
)
