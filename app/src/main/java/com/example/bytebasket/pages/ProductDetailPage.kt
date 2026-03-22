package com.example.bytebasket.pages

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

@Composable
fun ProductDetailPage(modifier: Modifier = Modifier, productId: String) {
    var product by rememberSaveable {
        mutableStateOf(
            Products(
                id = null, title = null, description = null,
                price = null, actualPrice = null, category = null,
                imageName = null, imageType = null, imageData = null,
                otherDetails = null
            )
        )
    }
    var fav by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    // ✅ Fetch product
    LaunchedEffect(Unit) {
        try {
            val response = ProductRetrofitInstance.api.getAllProductsById(productId.toLong())
            if (response.isSuccessful) {
                product = response.body()!!

                // check if favorite
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let {
                    Firebase.firestore.collection("user").document(it).get()
                        .addOnSuccessListener { document ->
                            val favoriteIds = document.get("favorite") as? List<String> ?: emptyList()
                            fav = favoriteIds.contains(productId)
                        }
                }
            }
        } catch (_: Exception) {}
    }

    val imageBytes = decodeBase64(product.imageData.toString())
    val bitmap = remember(imageBytes) {
        val options = BitmapFactory.Options().apply { inSampleSize = 2 }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 🏷️ Title
        Text(
            product.title?.replaceFirstChar { it.uppercase() } ?: "",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 🖼️ Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            AsyncImage(
                model = bitmap,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 💰 Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "₹${product.actualPrice?.toInt()}",
                fontSize = 16.sp,
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "₹${product.price?.toInt()}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD81B60)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ❤️ Fav Button + 🛒 Add to Cart together
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ❤️ Favorite Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFEBEE))
                    .clickable {
                        fav = !fav
                        if (fav) {
                            AddToFavorite(product.id.toString(), context)
                            AppUtil.showToast(context, "Added to Favorites ❤️")
                        } else {
                            RemoveFromFavorite(product.id.toString(), context)
                            AppUtil.showToast(context, "Removed from Favorites 💔")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (fav) Color.Red else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 🛒 Add to Cart
            Box(
                modifier = Modifier
                    .weight(3f)
                    .height(55.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF8E24AA), Color(0xFFD81B60))
                        )
                    )
                    .clickable {
                        AddItemToCart(modifier, productId, context)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Add to Cart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 📜 Description
        DetailSectionHeader("Product Description")
        Text(
            product.description ?: "",
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 📦 Other details
        DetailSectionHeader("Other Details")
        Column(modifier = Modifier.padding(top = 8.dp)) {
            product.otherDetails?.forEach { (k, v) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$k:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF424242),
                        modifier = Modifier.width(120.dp)
                    )
                    Text(v, fontSize = 16.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun DetailSectionHeader(title: String) {
    Column {
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            color = Color(0xFF3A0146)
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}
