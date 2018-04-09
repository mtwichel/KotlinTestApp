package com.marcustwichel.recipefinder.model

/**
 * Created by mtwichel on 4/9/18.
 */
data class KitchenItemList(var list : MutableList<String>) {

    fun getItem(pos :Int) : String{
        return list.get(pos)
    }

    constructor() : this(ArrayList<String>())
}