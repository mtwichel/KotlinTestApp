package com.example.android.kotlintest.api

import com.example.android.kotlintest.model.RecipieList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * Created by mtwichel on 3/14/18.
 */
interface RecipieAPI {
    @Headers({ "X-Mashape-Key: 7lqJ5Hl4RJmshs4bsOyrsroUiH2cp1pwFfjjsnU7UssW1DWdF3","Accept: application/json" })
    @GET("?key=fillIngredients=False&ingredients={ingredients}&limitLicense=False&number=5&ranking=1")

    fun getRecipies(@Path("ingredients") ingredients : String) : Call<RecipieList>
}