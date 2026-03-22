package com.example.bytebasket.pages

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bytebasket.Navigator.GlobalNavigator
import com.example.bytebasket.R
import com.example.bytebasket.components.AddItemToCart
import com.example.bytebasket.components.decodeBase64
import com.example.bytebasket.components.logRecentlyViewed
import com.example.bytebasket.products.ProductRetrofitInstance
import com.example.bytebasket.products.Products
import com.example.bytebasket.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Favorite(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var favoriteProducts by remember { mutableStateOf<List<Products>>(emptyList()) }
    var scale by remember { mutableStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 300),
        label = "Heart Scale Animation"
    )

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { document ->
                val favoriteIds = document.get("favorite") as? List<String> ?: emptyList()
                CoroutineScope(Dispatchers.IO).launch {
                    val tempProducts = mutableListOf<Products>()

                    favoriteIds.forEach { id ->
                        try {
                            val productResponse = ProductRetrofitInstance.api
                                .getAllProductsById(id.toLong())

                            if (productResponse.isSuccessful) {
                                productResponse.body()?.let { product ->
                                    tempProducts.add(product)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("FavoriteFetch", "Error: ${e.message}")
                        }
                    }

                    withContext(Dispatchers.Main) {
                        favoriteProducts = tempProducts
                    }
                }
            }
    }

    if (favoriteProducts.isEmpty()) {
        // ✨ Empty state UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No favorites yet!",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Start adding products you love ❤️",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(modifier = Modifier.padding(12.dp)) {
            items(favoriteProducts.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach {
                        FavCardView(product = it, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun FavCardView(
    product: Products,
    modifier: Modifier = Modifier
) {
    val imageBytes = decodeBase64(product.imageData.toString())
    val bitmap = remember(imageBytes) {
        val options = BitmapFactory.Options().apply { inSampleSize = 2 }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
    }
    val context = LocalContext.current

    Card(
        modifier = modifier
            .padding(6.dp)
            .clickable {
                GlobalNavigator.navHostController.navigate("ProductDetailPage/${product.id}")
                logRecentlyViewed(product.id.toString(), context)
            },
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🖼️ Product Image
            AsyncImage(
                model = bitmap,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🏷️ Product Title
            Text(
                text = product.title ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = Color(0xFF212121),
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 💰 Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${product.actualPrice?.toInt()}",
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₹${product.price?.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935) // 🔥 Highlighted discount
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🛒 Gradient Add to Cart Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF8E24AA), Color(0xFFD81B60))
                        )
                    )
                    .clickable {
                        AddItemToCart(modifier, product.id.toString(), context)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Add to Cart",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )
            }
        }
    }
}
