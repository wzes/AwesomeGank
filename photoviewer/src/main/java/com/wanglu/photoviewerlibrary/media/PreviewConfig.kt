package com.wanglu.photoviewerlibrary.media

import android.os.Parcel
import android.os.Parcelable

open class PreviewConfig(
    val mediaModelKey: String?,
    val currentPage: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mediaModelKey)
        parcel.writeInt(currentPage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PreviewConfig> {
        override fun createFromParcel(parcel: Parcel): PreviewConfig {
            return PreviewConfig(parcel)
        }

        override fun newArray(size: Int): Array<PreviewConfig?> {
            return arrayOfNulls(size)
        }
    }
}