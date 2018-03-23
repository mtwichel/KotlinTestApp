package com.example.android.kotlintest.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/14/18.
 */
data class Recipe (val id: Int,
                   val title: String,
                   val image: String,
                   val usedIngredientCount: Int,
                   val missedIngredientCount : Int,
                   val ingredients: List<Ingredient>,
                   val length: Length) : Serializable{

}