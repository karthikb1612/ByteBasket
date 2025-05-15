package com.example.bytebasket.products

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProductApiService {
    @Multipart
    @POST("/products/postProducts")
    suspend fun addProducts(
        @Part("product") product: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("/products/getAllProducts")
    suspend fun getAllProducts(): Response<List<Products>>

    @GET("/products/getByTitle/{category}")
    suspend fun getAllProductsByName(@Path("category") category: String): Response<List<Products>>

    @GET("/products/getById/{id}")
    suspend fun getAllProductsById(@Path("id") id: Long): Response<Products>
}