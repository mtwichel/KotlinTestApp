package com.example.android.kotlintest.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/16/18.
 */
data class Step(val number: Int, val step : String, val ingredients: List<Ingredient>) : Serializable{
}