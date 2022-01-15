package com.evosouza.libcarrousel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

object Data {
    /**
     * Mix of images with varying aspect ratios
     */
    val images = listOf(
        Image(R.drawable.img1, 600, 600),
        Image(R.drawable.img2, 600, 600),
        Image(R.drawable.img3, 700, 600),
        Image(R.drawable.img4, 500, 650),
        Image(R.drawable.img5, 600, 600),
        Image(R.drawable.img6, 600, 600),
        Image(R.drawable.img7, 600, 600),
        Image(R.drawable.img8, 600, 600),

    )
}

@Parcelize
data class Image(
    val url: Int,
    val width: Int,
    val height: Int
) : Parcelable {
    val aspectRatio: Float get() = width.toFloat() / height.toFloat()
}