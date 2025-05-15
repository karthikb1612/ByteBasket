package com.example.bytebasket.category

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class CategoryViewModel : ViewModel() {
    fun addCategory(context: Context, category: Category, imageUri: Uri) {
        viewModelScope.launch {
            val detailsJson = Gson().toJson(category)
            val detailsBody = detailsJson.toRequestBody("application/json".toMediaType())

            val imageFile = convertUriToFile(context, imageUri)
            val imageBody = imageFile.asRequestBody("image/*".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageBody)
            try {
                val response = CategoryRetrofitInstance.api.addCategory(detailsBody, imagePart)
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
}