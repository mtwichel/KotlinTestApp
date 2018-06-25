package com.marcustwichel.recipefinder.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


interface FirebaseAPI {


    @GET("moveCheckedToKitchen")
    fun moveCheckedToKitchen(@Header("secret") hash : String, @Query("userId") userId : String) : Call<String>

}