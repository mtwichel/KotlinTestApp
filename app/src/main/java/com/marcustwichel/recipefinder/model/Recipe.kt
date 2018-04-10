package com.marcustwichel.recipefinder.recipefinder.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/14/18.
 */
data class Recipe (val id: Int,
                   val title: String,
                   val image: String,
                   val usedIngredientCount: Int,
                   val missedIngredientCount : Int
                        ) : Serializable{

}