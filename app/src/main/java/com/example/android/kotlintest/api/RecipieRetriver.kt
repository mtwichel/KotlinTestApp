package com.example.android.kotlintest.api

import com.example.android.kotlintest.model.RecipieList
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder

/**
 * Created by marcus.twichel on 3/14/18.
 */
class RecipieRetriver {

    private val service : RecipieAPI

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(RecipieAPI::class.java)
    }

    fun getRecipies(callback: Callback<RecipieList>) {
        val call = service.getRecipies(URLEncoder.encode("apples,sugar,flour", "UTF-8"))
        call.enqueue(callback)
    }
}