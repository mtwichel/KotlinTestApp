package com.marcustwichel.recipefinder.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable
import android.widget.RelativeLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R


class KitchenItemAdapter(var kitchenItems : ArrayList<String>) : RecyclerView.Adapter<KitchenItemAdapter.KitchenItemViewHolder>(), Serializable{

    val TAG = "KitchenItemAdapter"


    lateinit var items : ArrayList<String>



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitchenItemViewHolder {
        return KitchenItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.kitchen_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: KitchenItemViewHolder, position: Int) {
        holder?.itemName?.setText(items?.get(position))
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

    fun getItemName(adapterPosition: Int): String {
        return items.get(adapterPosition)
    }
}