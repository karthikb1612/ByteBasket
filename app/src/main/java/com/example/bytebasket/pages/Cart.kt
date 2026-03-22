package com.example.bytebasket.pages

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.bytebasket.Navigator.GlobalNavigator
import com.example.bytebasket.components.AddItemToCart
import com.example.bytebasket.components.RemoveItemToCart
import com.example.bytebasket.components.decodeBase64
import com.example.bytebasket.model.UserModel
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
fun Cart(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var cartItems by remember { mutableStateOf<List<Pair<Products, Long>>>(emptyList()) }
    var userModel by remember { mutableStateOf(UserModel()) }

    // Load user + cart data
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel = result
                        CoroutineScope(Dispatchers.IO).launch {
                            val tempList = mutableListOf<Pair<Products, Long>>()
                            for ((productId, quantity) in result.cartItems) {
                                try {
                                    val productResponse = ProductRetrofitInstance.api
                                        .getAllProductsById(productId.toLong())
                                    productResponse.body()?.let { product ->
                                        tempList.add(product to quantity)
                                    }
                                } catch (_: Exception) {}
                            }
                            withContext(Dispatchers.Main) {
                                cartItems = tempList
                            }
                        }
                    }
                }
            }
    }

    val totalPrice = cartItems.sumOf { (product, qty) ->
        (product.price?.toInt() ?: 0) * qty
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Text(
            text = "🛒 Your Cart (${cartItems.size} items)",
            modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color(0xFF3A0146)
        )

        // Cart items
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            items(cartItems) { (product, quantity) ->
                CartItemView(product, quantity) { productId, newQuantity ->
                    val updatedCartItems = cartItems.toMutableList()
                    val index = updatedCartItems.indexOfFirst { it.first.id == productId.toLong() }
                    if (index != -1) {
                        if (newQuantity <= 0) {
                            updatedCartItems.removeAt(index)
                        } else {
                            updatedCartItems[index] =
                                updatedCartItems[index].copy(second = newQuantity.toLong())
                        }
                        cartItems = updatedCartItems
                    }
                }
            }
        }

        // Checkout bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: ₹$totalPrice",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { /* Checkout logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A0146)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                Text("Checkout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun CartItemView(
    product: Products,
    quantity: Long,
    onQuantityChange: (String, Int) -> Unit
) {
    val context = LocalContext.current
    val quan = rememberSaveable { mutableStateOf(quantity.toString()) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                GlobalNavigator.navHostController
                    .navigate("ProductDetailPage/${product.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            val imageBytes = decodeBase64(product.imageData.toString())
            val bitmap = remember(imageBytes) {
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }
            AsyncImage(
                model = bitmap,
                contentDescription = product.title,
                modifier = Modifier
                    .size(90.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = product.title ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.actualPrice?.toInt()}",
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹${product.price?.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A0146)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    FloatingActionButton(
                        onClick = {
                            val newQuantity = quan.value.toInt() - 1
                            if (newQuantity >= 0) {
                                RemoveItemToCart(Modifier, product.id.toString(), context)
                                onQuantityChange(product.id.toString(), newQuantity)
                                quan.value = newQuantity.toString()
                            }
                        },
                        containerColor = Color(0xFFF2E6F9),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-", color = Color(0xFF3A0146), fontSize = 18.sp)
                    }

                    Text(
                        text = quan.value,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    FloatingActionButton(
                        onClick = {
                            val newQuantity = quan.value.toInt() + 1
                            onQuantityChange(product.id.toString(), newQuantity)
                            AddItemToCart(Modifier, product.id.toString(), context)
                            quan.value = newQuantity.toString()
                        },
                        containerColor = Color(0xFFF2E6F9),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+", color = Color(0xFF3A0146), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}



