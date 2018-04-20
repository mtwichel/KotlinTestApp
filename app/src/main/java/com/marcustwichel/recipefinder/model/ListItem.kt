package com.marcustwichel.recipefinder.model

import java.io.Serializable

/**
 * Created by mtwichel on 4/18/18.
 */
data class ListItem(val id : Int, val string : String, val checked : Boolean) : Comparable<ListItem?>, Serializable{
    constructor() : this(0, "", false)

    override fun compareTo(other: ListItem?): Int {
        return other!!.id - this.id
    }

}