package com.example.bytebasket.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Category(
    val categoryId: Long?,
    val categoryName: String,
    var imageName: String?,
    val imageType: String?,
    val imageData: String?
):Parcelable
