package com.example.bytebasket.products

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bytebasket.category.convertUriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.runtime.State

class ProductViewModel : ViewModel() {
    private val _searchResults = mutableStateOf<List<Products>>(emptyList())
    val searchResults: State<List<Products>> = _searchResults
    fun addProducts(context: Context, products: Products, imageUri: Uri) {
        viewModelScope.launch {
            val detailsJson = Gson().toJson(products)
            val detailsBody = detailsJson.toRequestBody("application/json".toMediaType())

            val imageFile = convertUriToFile(context, imageUri)
            val imageBody = imageFile.asRequestBody("image/*".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageBody)
            try {
                val response = ProductRetrofitInstance.api.addProducts(detailsBody, imagePart)
                if (response.isSuccessful) {
                    Log.d("Success", "Data uploaded successfully")
                } else {
                    Log.e("Error", "Failed to upload data: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("StudentViewModel", "Error uploading student data: ${e.message}")
            }
        }
    }
    fun getProductsBySearchKeyWord(context: Context, keyword: String) {
        viewModelScope.launch {
            if (keyword.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }

            try {
                val response = ProductRetrofitInstance.api.getProductBySearch(keyword)
                if (response.isSuccessful && response.body() != null) {
                    _searchResults.value = response.body()!! // ✅ FIXED LINE
                    Log.d("Search", "Found: ${response.body()?.size} products")
                } else {
                    Log.e("Search", "Error: ${response.errorBody()?.string()}")
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("Search", "Exception: ${e.localizedMessage}")
                _searchResults.value = emptyList()
            }
        }
    }


}