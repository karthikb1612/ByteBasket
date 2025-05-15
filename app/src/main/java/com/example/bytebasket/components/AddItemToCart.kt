package com.example.bytebasket.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bytebasket.AppUtil.showToast
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


fun AddItemToCart(modifier: Modifier = Modifier,productId : String,context: Context) {
    val userDoc = Firebase.firestore.collection("user").
            document(FirebaseAuth.getInstance().currentUser?.uid!!)
    userDoc.get().addOnCompleteListener {
        if(it.isSuccessful){
            val currentCart=it.result.get("cartItems") as? Map<String,Long>  ?: emptyMap()
            val currentQuantity=currentCart[productId]?:0
            val updatedQuantity=currentQuantity+1
            val updatedCart=mapOf("cartItems.$productId" to updatedQuantity)
            userDoc.update(updatedCart).addOnCompleteListener {
                if(it.isSuccessful){
                    showToast(context,"Item added to the Cart")
                }else{
                    showToast(context,"Failed adding item to the cart ")
                }
            }
        }
    }

}
fun RemoveItemToCart(modifier: Modifier = Modifier,productId : String,context: Context) {
    val userDoc = Firebase.firestore.collection("user").
    document(FirebaseAuth.getInstance().currentUser?.uid!!)
    userDoc.get().addOnCompleteListener {
        if(it.isSuccessful){
            val currentCart=it.result.get("cartItems") as? Map<String,Long>  ?: emptyMap()
            val currentQuantity=currentCart[productId]?:0
            val updatedQuantity=currentQuantity-1
            val updateMap = if (updatedQuantity <= 0) {
                mapOf("cartItems.$productId" to FieldValue.delete())
            } else {
                mapOf("cartItems.$productId" to updatedQuantity)
            }
            userDoc.update(updateMap).addOnCompleteListener {
                if(it.isSuccessful){
                    showToast(context,"Item removed from Cart")
                }else{
                    showToast(context,"Failed to remove from cart ")
                }
            }
        }
    }

}