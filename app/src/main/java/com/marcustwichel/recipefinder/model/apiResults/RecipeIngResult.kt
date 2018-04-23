package com.marcustwichel.recipefinder.recipefinder.model

/**
 * Created by mtwichel on 4/5/18.
 */
data class RecipeIngResult(val extendedIngredients: List<Ingredient>,
                           val sourceUrl : String,
                           val sourceName : String,
                           val readyInMinutes : Int){
}