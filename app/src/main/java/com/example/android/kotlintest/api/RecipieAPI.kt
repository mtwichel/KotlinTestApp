package com.example.android.kotlintest.api

import com.example.android.kotlintest.model.Recipe
import com.example.android.kotlintest.model.RecipeSteps
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by mtwichel on 3/14/18.
 */
interface RecipieAPI {
    @Headers("X-Mashape-Key: 7lqJ5Hl4RJmshs4bsOyrsroUiH2cp1pwFfjjsnU7UssW1DWdF3", "Accept: application/json")
    @GET("recipes/findByIngredients/?limitLicense=False&fillIngredients=False&number=10&ranking=1")
    fun getRecipes(@Query("ingredients") ingredients: String): Call<List<Recipe>>

    @Headers("X-Mashape-Key: 7lqJ5Hl4RJmshs4bsOyrsroUiH2cp1pwFfjjsnU7UssW1DWdF3")
    @GET("recipes/{id}/analyzedInstructions?stepBreakdown=true")
    fun getRecipeById(@Path("id") recipeId: Int): Call<List<RecipeSteps>>

}