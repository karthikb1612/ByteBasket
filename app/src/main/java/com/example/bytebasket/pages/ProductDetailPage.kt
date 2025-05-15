package com.example.bytebasket.pages


import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytebasket.AppUtil
import com.example.bytebasket.components.AddItemToCart
import com.example.bytebasket.components.AddToFavorite
import com.example.bytebasket.components.RemoveFromFavorite
import com.example.bytebasket.components.decodeBase64
import com.example.bytebasket.products.ProductRetrofitInstance
import com.example.bytebasket.products.Products
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProductDetailPage(modifier: Modifier = Modifier, productId: String) {
    var product by rememberSaveable { mutableStateOf(Products(
        id = null,
        title = null,
        description = null,
        price = null,
        actualPrice = null,
        category = null,
        imageName = null,
        imageType = null,
        imageData = null,
        otherDetails = null
    )) }
    var fav by rememberSaveable { mutableStateOf(false) }
    var context = LocalContext.current
    
    LaunchedEffect(Unit) {
        try {
            val response = ProductRetrofitInstance.api.getAllProductsById(productId.toLong())
            if (response.isSuccessful) {
                product = response.body()!!

                // Check if this product is already favorite
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let {
                    Firebase.firestore.collection("user").document(it).get()
                        .addOnSuccessListener { document ->
                            val favoriteIds = document.get("favorite") as? List<String> ?: emptyList()
                            fav = favoriteIds.contains(productId)
                        }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val imageBytes = decodeBase64(product.imageData.toString())
    val bitmap = remember(imageBytes) {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 2
        }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size,options)
    }
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).
        verticalScroll(rememberScrollState())
    ) {
        Text(
            product.title.toString().toUpperCase(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        AsyncImage(
            model = bitmap,
            contentDescription = product.title,
            modifier = Modifier.fillMaxWidth().height(300.dp).
            align(alignment = Alignment.CenterHorizontally).clip(shape = RoundedCornerShape(20))
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹"+ product.actualPrice?.toInt().toString(),
                fontSize = 18.sp,
                textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "₹"+product.price?.toInt().toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    fav = !fav
                    if (fav) {
                        AddToFavorite(product.id.toString(), context)
                        AppUtil.showToast(context, "Product added to Favorite")
                    } else {
                        RemoveFromFavorite(product.id.toString(), context)
                        AppUtil.showToast(context, "Product removed from Favorite")
                    }
                },

            ) {
                Icon(
                    imageVector = if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (fav) "Added to Favorite" else "Add to Favorite",
                    tint = if (fav) Color.Red else Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FloatingActionButton(
            onClick = {
                AddItemToCart(modifier,productId,context)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            containerColor = Color(0xF0F1E57F)
        ) {
            Text("Add to Cart", fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3A0146)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Product Description :",
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            product.description.toString(),
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Other Details :",
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
        product.otherDetails?.forEach { (k,v) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Text("$k: ", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(v, fontSize = 16.sp)
            }
        }
    }

}


