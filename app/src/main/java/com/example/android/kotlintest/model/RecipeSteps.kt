package com.example.android.kotlintest.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/16/18.
 */
data class RecipeSteps(val steps: List<Step>) : Serializable {
}