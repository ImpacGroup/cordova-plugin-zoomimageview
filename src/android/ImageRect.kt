package de.impacgroup.zoomimageview.module

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class ImageRect(

    @SerializedName("x")
    var x: Float,
    @SerializedName("y")
    var y: Float,
    @SerializedName("width")
    var width: Int,
    @SerializedName("height")
    var height: Int
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

    fun toDp(context: Context): ImageRect {
        val pxX = pxFromDp(context, x)
        val pxY = pxFromDp(context, y)
        val pxWidth = pxFromDp(context, width.toFloat())
        val pxHeight = pxFromDp(context, height.toFloat())
        return ImageRect(pxX, pxY, pxWidth.toInt(), pxHeight.toInt())
    }

    open fun pxFromDp(context: Context, dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}