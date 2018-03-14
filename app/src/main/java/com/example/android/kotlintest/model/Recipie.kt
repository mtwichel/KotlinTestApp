package com.example.android.kotlintest.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/14/18.
 */
data class Recipie (val id: Int,
                    val title: String,
                    val imgUrl: String,
                    val ingredients: List<Ingredient>,
                    val length: Length,
                    val steps: String) : Serializable{

}