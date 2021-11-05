package de.impacgroup.zoomimageview

import com.google.gson.annotations.SerializedName

open class ImageInfo(

    @SerializedName("image")
    val image: String,
    @SerializedName("closeButton")
    val closeButton: Boolean,
    @SerializedName("imageRect")
    val imageRect: ImageRect?
)

open class ImageRect(

    @SerializedName("x")
    val x: Int,
    @SerializedName("y")
    val y: Int,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
)
