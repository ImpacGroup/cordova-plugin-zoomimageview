package de.impacgroup.zoomimageview.module

import android.os.Parcel
import android.os.Parcelable

data class ImagePosition(
    val rect: ImageRect,
    val orientation: Int
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(ImageRect::class.java.classLoader)!!,
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(rect, flags)
        parcel.writeInt(orientation)
    }

    companion object CREATOR : Parcelable.Creator<ImagePosition> {
        override fun createFromParcel(parcel: Parcel): ImagePosition {
            return ImagePosition(parcel)
        }

        override fun newArray(size: Int): Array<ImagePosition?> {
            return arrayOfNulls(size)
        }
    }
}
