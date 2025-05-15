package com.example.bytebasket.model

data class UserModel(
    val phoneNo: String ="",
    val name: String = "",
    val email: String = "",
    val uid : String = "",
    val cartItems : Map<String, Long> = mapOf()
)
