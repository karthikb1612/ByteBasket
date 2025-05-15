package com.example.bytebasket.components

import android.content.Context
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bytebasket.Navigator.GlobalNavigator
import com.example.bytebasket.products.Products
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun ProductItemView(modifier: Modifier = Modifier, items: Products) {
    ProductItemCard(items,modifier)
}

@Composable
fun ProductItemCard(
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
        }
    }
}
fun logRecentlyViewed(productId: String, context: Context) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val userDoc = Firebase.firestore.collection("user").document(userId)

    // Add to beginning of recentViewed list (maintain only last 10)
    userDoc.get().addOnSuccessListener { it ->
        val existing = it.get("recentViewed") as? List<String> ?: emptyList()
        val updatedList = listOf(productId) + existing.filter { it != productId }.take(9)

        userDoc.update("recentViewed", updatedList)
    }
}



