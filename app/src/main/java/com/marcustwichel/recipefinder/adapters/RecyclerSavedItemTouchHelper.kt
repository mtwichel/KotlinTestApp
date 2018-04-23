package com.marcustwichel.recipefinder.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Created by mtwichel on 4/23/18.
 */
class RecyclerSavedItemTouchHelper(dragDirs : Int, swipeDirs : Int, val listener: RecyclerSavedItemTouchHelper.SavedRecipeSwipeListener) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder?.getAdapterPosition())
    }

    interface SavedRecipeSwipeListener{
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?)
    }
}