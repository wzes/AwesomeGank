package com.wanglu.photoviewerlibrary

data class MediaModel(
    val url: String,
    val width: Int,
    val height: Int,
    val locationRec: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaModel

        if (url != other.url) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (!locationRec.contentEquals(other.locationRec)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + locationRec.contentHashCode()
        return result
    }
}