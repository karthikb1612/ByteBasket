package com.example.bytebasket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.bytebasket.Navigator.AppNavigator
import com.example.bytebasket.category.CategoryViewModel
import com.example.bytebasket.products.ProductViewModel
import com.example.bytebasket.ui.theme.ByteBasketTheme
import com.example.bytebasket.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel= ViewModelProvider(this)[AuthViewModel::class.java]
        val categoryViewModel= ViewModelProvider(this)[CategoryViewModel::class.java]
        val productViewModel= ViewModelProvider(this)[ProductViewModel::class.java]
        setContent {
            ByteBasketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigator(modifier = Modifier.padding(innerPadding),viewModel,categoryViewModel,productViewModel)
                }
            }
        }
    }
}

