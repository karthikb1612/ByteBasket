package com.example.bytebasket.pages

import android.graphics.BitmapFactory
import androidx.compose.foundation.clickable
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
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("user").
        document(FirebaseAuth.getInstance().currentUser?.uid!!).get().
                addOnCompleteListener{
                    if(it.isSuccessful){
                        val result= it.result.toObject(UserModel::class.java)
                        if(result!=null){
                            userModel=result;

                            CoroutineScope(Dispatchers.IO).launch {
                                val tempList = mutableListOf<Pair<Products, Long>>()

                                for ((productId, quantity) in result.cartItems) {
                                    try {
                                        val productResponse = ProductRetrofitInstance.api
                                            .getAllProductsById(productId.toLong())

                                        productResponse.body()?.let { product ->
                                            tempList.add(product to quantity)
                                        }
                                    } catch (e: Exception) {
                                        e.message
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    cartItems = tempList
                                }
                            }
                        }
                    }
                }
    }
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(cartItems) { (product, quantity) ->
            CartItemView(product, quantity) { productId, newQuantity ->
                val updatedCartItems = cartItems.toMutableList()
                val index = updatedCartItems.indexOfFirst { it.first.id == productId.toLong() }
                if (index != -1) {
                    if (newQuantity <= 0) {
                        updatedCartItems.removeAt(index)
                    }else{
                        updatedCartItems[index] = updatedCartItems[index].copy(second = newQuantity.toLong())
                    }
                    cartItems = updatedCartItems
                }
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
            .padding(8.dp)
            .height(200.dp)
            .clickable(
                onClick = {
                    GlobalNavigator.navHostController.navigate("ProductDetailPage/"+product.id.toString())
                }
            ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            val imageBytes = decodeBase64(product.imageData.toString())
            val bitmap = remember(imageBytes) {
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2
                }
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size,options)
            }

            AsyncImage(
                model = bitmap,
                contentDescription = product.title,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            )

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = product.title ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value =quan.value,
                    onValueChange = { quan.value = it },
                    label = { Text("Quantity") },
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.height(60.dp).width(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Text(
                        text = "₹${product.actualPrice?.toInt()}",
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${product.price?.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                        FloatingActionButton(
                            onClick = {
                                val newQuantity = quan.value.toInt() - 1
                                RemoveItemToCart(Modifier,product.id.toString(),context)
                                onQuantityChange(product.id.toString(), newQuantity)
                                quan.value = newQuantity.toString()
                            },
                            containerColor = Color(0xF0F1E57F),
                        ) {
                            Text("Remove", color = Color(0xFF3A0146))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        FloatingActionButton(
                            onClick = {
                                val newQuantity = quan.value.toInt() + 1
                                onQuantityChange(product.id.toString(), newQuantity)
                                AddItemToCart(Modifier,product.id.toString(),context)
                                quan.value = newQuantity.toString()
                            },
                            containerColor = Color(0xF0F1E57F)
                        ) {
                            Text("Add", color = Color(0xFF3A0146))
                        }
                    }
                }
        }
    }
}
