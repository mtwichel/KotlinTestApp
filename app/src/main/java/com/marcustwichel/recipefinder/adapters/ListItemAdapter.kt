package com.marcustwichel.recipefinder.adapters

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R
import java.io.Serializable
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.marcustwichel.recipefinder.model.ListItem
import java.util.*


class ListItemAdapter(val clickListener: View.OnClickListener, val recyclerView: RecyclerView, var items : ArrayList<ListItem?>) :  RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>(), Serializable {


    val TAG = "ListItemAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = ListItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item, parent, false))
        return view
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val text = holder.listText
        text?.text = items[position]?.string
        if(items[position]!!.checked){
            text?.setPaintFlags(text?.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            holder.viewForeground?.setBackgroundColor(Color.LTGRAY)
        }
    }

    inner class ListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var listText : TextView?

        var viewBackground : RelativeLayout?
        var viewForeground : LinearLayout?

        init{
            itemView.setOnClickListener(clickListener)
            itemView.tag = this
            listText = itemView.findViewById(R.id.list_text)
            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)
        }
    }
}

