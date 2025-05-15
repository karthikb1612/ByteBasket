package com.example.bytebasket.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebasket.model.UserModel
import com.example.bytebasket.products.ProductRetrofitInstance
import com.example.bytebasket.products.Products
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AuthViewModel : ViewModel() {
    private val auth= Firebase.auth
    private val firestore= Firebase.firestore
    fun login(email: String,password: String,onResult: (Boolean, String?) -> Unit){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                onResult(true,null)
            }else{
                onResult(false,it.exception?.localizedMessage)
            }
        }

    }
    fun signUp(name : String,phoneNo : String,email : String,password: String,onResult : (Boolean, String?) -> Unit){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                var userId=it.result?.user?.uid
                val userModel = UserModel(name = name, phoneNo = phoneNo, email = email, uid = userId!!)
                firestore.collection("user").document(userId).set(userModel)
                    .addOnCompleteListener { taskdp ->
                        if(taskdp.isSuccessful){
                            onResult(true,null)
                        }else{
                            onResult(false,"Something went wrong")
                        }
                    }

            }else{
                onResult(false,it.exception?.localizedMessage)
            }
        }
    }
    fun signOut(){
        auth.signOut()
    }


}