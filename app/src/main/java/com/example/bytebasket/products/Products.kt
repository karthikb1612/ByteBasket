package com.example.bytebasket.products

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(
    val id: Long?,
    val title: String?,
    val description: String?,
    val price: Double?,
    val actualPrice: Double?,
    val category: String?,
    val imageName: String?,
    val imageType: String?,
    val imageData: String?,
    val otherDetails: MutableMap<String, String>? = mutableMapOf()
): Parcelable

