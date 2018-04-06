package com.example.android.kotlintest.model

import java.io.Serializable

/**
 * Created by mtwichel on 3/14/18.
 */
data class Ingredient (val id: Int,
                       val name: String,
                       val image: String,
                       val unit: String,
                       val amount: Double,
                       val unitLong: String) : Serializable {

}