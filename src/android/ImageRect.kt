package de.impacgroup.zoomimageview.module

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class ImageRect(

    @SerializedName("x")
    val x: Float,
    @SerializedName("y")
    val y: Float,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(x)
        parcel.writeFloat(y)
        parcel.writeInt(width)
        parcel.writeInt(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageRect> {
        override fun createFromParcel(parcel: Parcel): ImageRect {
            return ImageRect(parcel)
        }

        override fun newArray(size: Int): Array<ImageRect?> {
            return arrayOfNulls(size)
        }
    }
}