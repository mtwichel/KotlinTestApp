package com.example.android.kotlintest.model

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.android.kotlintest.DetailStepsAdapter
import com.example.android.kotlintest.R
import java.io.Serializable
import android.content.ClipData.Item
import android.widget.RelativeLayout


/**
 * Created by marcus.twichel on 4/6/18.
 */
class KitchenItemAdapter(val  items: MutableList<String>) : RecyclerView.Adapter<KitchenItemAdapter.KitchenItemViewHolder>(), Serializable{
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): KitchenItemViewHolder {
        return KitchenItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.kitchen_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: KitchenItemViewHolder?, position: Int) {
        holder?.itemName?.setText(items?.get(position))
    }

    fun removeItem(position: Int) {
        items?.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: String, position: Int) {
        items.add(position, item)
        // notify item added by position
        notifyItemInserted(position)
    }


    inner class KitchenItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName : TextView?
        var viewBackground : RelativeLayout?
        var viewForeground : RelativeLayout?

        init {

            itemView.tag = this

            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)


            itemName = itemView.findViewById(R.id.item_name) as TextView

        }
    }
}