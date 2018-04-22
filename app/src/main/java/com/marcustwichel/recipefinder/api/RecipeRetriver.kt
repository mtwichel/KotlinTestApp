package com.marcustwichel.recipefinder.recipefinder.api

import android.util.Log
import com.marcustwichel.recipefinder.model.AutocompleteResult
import com.marcustwichel.recipefinder.model.RecipeSearchResult
import com.marcustwichel.recipefinder.recipefinder.model.Recipe
import com.marcustwichel.recipefinder.recipefinder.model.RecipeIngResult
import com.marcustwichel.recipefinder.recipefinder.model.RecipeStepsResult
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by marcus.twichel on 3/14/18.
 */
class RecipeRetriver {

    val TAG = "RecipeRetriver"

    private val service : RecipieAPI

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(RecipieAPI::class.java)
    }

    fun getRecipes(callback: Callback<RecipeSearchResult>,
                   searchString : String?,
                   cuisine : String?,
                   type : String?,
                   queryString : String?,
                   ranking : Int?) {
        val call = service.getRecipes(searchString, cuisine, type, queryString, ranking)
        Log.d(TAG, call.request().url().toString())
        call.enqueue(callback)
    }

    fun getRecipeStepsById(callback: Callback<List<RecipeStepsResult>>, id: Int){
        val call = service.getRecipeStepsById(id)
        call.enqueue(callback)
    }

    fun getRecipeIngById(callback: Callback<RecipeIngResult>, id: Int){
        val call = service.getRecipeIngById(id)
        Log.d(TAG, call.request().url().toString())
        call.enqueue(callback)
    }

    fun getIngredientsAutocomplete(callback: Callback<List<AutocompleteResult>>, query: String){
        val call = service.getIngredientsAutocomplete(query)
        Log.d(TAG, call.request().url().toString())
        call.enqueue(callback)
    }
}
