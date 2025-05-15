package com.example.bytebasket.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bytebasket.components.ProductItemView
import com.example.bytebasket.products.ProductRetrofitInstance
import com.example.bytebasket.products.Products

@Composable
fun CategoryProducts(modifier: Modifier = Modifier, categoryId: String) {
    var searchQuery by remember { mutableStateOf("") }
    var productList by rememberSaveable { mutableStateOf<List<Products>>(emptyList()) }
    LaunchedEffect(Unit) {
        productList= ProductRetrofitInstance.api.getAllProductsByName(categoryId).body() ?: emptyList()
    }
    Column{
        Spacer(modifier = Modifier.height(12.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier= Modifier.fillMaxWidth().padding(8.dp,20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(productList.chunked(2)) { Rowitems ->
                Row {
                    Rowitems.forEach {
                        ProductItemView(modifier = Modifier.weight(1f),it)
                    }
                    if(Rowitems.size==1){
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

}