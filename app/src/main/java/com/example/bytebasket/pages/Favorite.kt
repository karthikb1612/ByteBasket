package com.example.bytebasket.pages

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
import com.example.bytebasket.components.AddItemToCart
import com.example.bytebasket.components.ProductItemView
import com.example.bytebasket.components.RemoveItemToCart
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

                    // After fetching all products, update the UI on Main thread
                    withContext(Dispatchers.Main) {
                        favoriteProducts = tempProducts
                    }
                }
            }
    }
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(favoriteProducts.chunked(2)) { Rowitems ->
            Row {
                Rowitems.forEach {
                    FavCardView(product = it,modifier = Modifier.weight(1f))
                }
                if(Rowitems.size==1){
                    Spacer(modifier = Modifier.weight(1f))
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
        val options = BitmapFactory.Options().apply {
            inSampleSize = 2 // Helps reduce memory usage without resizing on screen
        }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size,options)
    }
    var context=LocalContext.current
    Card(
        modifier = modifier
            .padding(8.dp).
            clickable{
                GlobalNavigator.navHostController.navigate("ProductDetailPage/"+product.id)
                logRecentlyViewed(product.id.toString(), context)
            }
        ,
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = bitmap,
                contentDescription = product.title,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Text(
                product.title.toString(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹"+ product.actualPrice?.toInt().toString(),
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "₹"+product.price?.toInt().toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            FloatingActionButton(
                onClick = {
                    AddItemToCart(modifier,product.id.toString(),context)
                },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                containerColor = Color(0xF0F1E57F)
            ) {
                Text("Add to Cart", fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3A0146)
                )
            }
        }
    }
}