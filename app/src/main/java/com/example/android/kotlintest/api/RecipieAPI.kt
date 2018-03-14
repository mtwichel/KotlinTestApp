package com.example.android.kotlintest.api

import com.example.android.kotlintest.model.RecipieList
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by mtwichel on 3/14/18.
 */
interface RecipieAPI {
    @GET(?Key=)
    fun getRecipies() : Call<RecipieList>
}