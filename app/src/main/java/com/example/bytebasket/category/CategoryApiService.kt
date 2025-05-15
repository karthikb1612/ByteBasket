package com.example.bytebasket.category

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CategoryApiService {
    @Multipart
    @POST("/category/post")
    suspend fun addCategory(
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("/category/getAll")
    suspend fun getAllCategory(): Response<List<Category>>
}