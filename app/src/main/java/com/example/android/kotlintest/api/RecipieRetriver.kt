package com.example.android.kotlintest.api

import android.util.Log
import com.example.android.kotlintest.model.Recipe
import com.example.android.kotlintest.model.RecipeIngResult
import com.example.android.kotlintest.model.RecipeStepsResult
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by marcus.twichel on 3/14/18.
 */
class RecipieRetriver {

    private val service : RecipieAPI

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(RecipieAPI::class.java)
    }

    fun getRecipes(callback: Callback<List<Recipe>>, searchString : String ) {
        val call = service.getRecipes(searchString)
        Log.d("RecipeRetriver", call.request().url().toString())
        call.enqueue(callback)
    }

    fun getRecipeStepsById(callback: Callback<List<RecipeStepsResult>>, id: Int){
        val call = service.getRecipeStepsById(id)
        Log.d("RecipeRetriver", call.request().url().toString())
        call.enqueue(callback)
    }

    fun getRecipeIngById(callback: Callback<RecipeIngResult>, id: Int){
        val call = service.getRecipeIngById(id)
        Log.d("RecipeRetriver", call.request().url().toString())
        call.enqueue(callback)
    }
}
