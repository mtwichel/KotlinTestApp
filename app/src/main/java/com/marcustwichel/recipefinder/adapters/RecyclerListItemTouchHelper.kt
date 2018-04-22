package com.marcustwichel.recipefinder.adapters

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Created by marcus.twichel on 4/6/18.
 */
class RecyclerListItemTouchHelper(dragDirs : Int, swipeDirs : Int, listener: RecyclerItemTouchHelperListener)
        : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {


    private var listener : RecyclerItemTouchHelperListener? = null;

    init {
        this.listener = listener
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = (viewHolder as ListItemAdapter.ListItemViewHolder).viewForeground

            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView,
                                 viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                                 actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as ListItemAdapter.ListItemViewHolder).viewForeground
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = (viewHolder as ListItemAdapter.ListItemViewHolder).viewForeground
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = (viewHolder as ListItemAdapter.ListItemViewHolder).viewForeground

        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive)
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        listener?.onSwiped(viewHolder, direction, viewHolder?.getAdapterPosition())
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    public interface RecyclerItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?)
    }
}