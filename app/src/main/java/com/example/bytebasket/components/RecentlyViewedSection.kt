package com.example.bytebasket.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytebasket.Navigator.GlobalNavigator
import com.example.bytebasket.products.ProductRetrofitInstance
import com.example.bytebasket.products.Products
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun RecentlyViewedSection(modifier: Modifier) {
    var recentProducts by rememberSaveable { mutableStateOf<List<Products>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid!!
            Firebase.firestore.collection("user")
                .document(userId).get()
                .addOnSuccessListener { doc ->
                    val productIds = doc.get("recentViewed") as? List<String> ?: emptyList()

                    coroutineScope.launch {
                        val tempList = mutableListOf<Products>()
                        for (id in productIds) {
                            try {
                                val product = ProductRetrofitInstance.api.getAllProductsById(id.toLong()).body()
                                if (product != null) {
                                    tempList.add(product)
                                }
                            } catch (e: Exception) {
                                Log.e("Error",e.message.toString())
                            }
                        }
                        recentProducts = tempList
                    }
                }
    }
    if (recentProducts.isNotEmpty()) {
        Column {
            recentProducts.chunked(2).forEach { rowItems ->
                Row {
                    rowItems.forEach {
                        ProductCard(modifier = Modifier.weight(1f),product = it)
                    }
                    if(rowItems.size==1){
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

    }

}


@Composable
fun ProductCard(product: Products, modifier: Modifier) {
    val imageBytes = decodeBase64(product.imageData ?: "")
    val bitmap = remember(imageBytes) {
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                GlobalNavigator.navHostController.navigate("ProductDetailPage/${product.id}")
            },
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
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                product.title.toString(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}



