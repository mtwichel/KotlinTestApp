package com.marcustwichel.recipefinder.recipefinder.api

import com.marcustwichel.recipefinder.model.RecipeSearchResult
import com.marcustwichel.recipefinder.recipefinder.model.Recipe
import com.marcustwichel.recipefinder.recipefinder.model.RecipeIngResult
import com.marcustwichel.recipefinder.recipefinder.model.RecipeStepsResult
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
    @GET("recipes/searchComplex/?limitLicense=False&fillIngredients=False&number=10&offset=0&instructionsRequired=true")
    fun getRecipes(@Query("includeIngredients") ingredients: String,
                   @Query("type") type: String?,
                   @Query("cuisine") cuisine : String?,
                   @Query("query") queryString : String?,
                   @Query("ranking") ranking : Int?): Call<RecipeSearchResult>

    @Headers("X-Mashape-Key: 7lqJ5Hl4RJmshs4bsOyrsroUiH2cp1pwFfjjsnU7UssW1DWdF3")
    @GET("recipes/{id}/analyzedInstructions?stepBreakdown=true")
    fun getRecipeStepsById(@Path("id") recipeId: Int): Call<List<RecipeStepsResult>>

    @Headers("X-Mashape-Key: 7lqJ5Hl4RJmshs4bsOyrsroUiH2cp1pwFfjjsnU7UssW1DWdF3")
    @GET("recipes/{id}/information")
    fun getRecipeIngById(@Path("id") recipeId: Int): Call<RecipeIngResult>

}