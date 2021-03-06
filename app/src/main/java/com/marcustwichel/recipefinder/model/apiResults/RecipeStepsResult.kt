package com.marcustwichel.recipefinder.recipefinder.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/16/18.
 */
data class RecipeStepsResult(val name: String, val steps: List<Step>) : Serializable {
}