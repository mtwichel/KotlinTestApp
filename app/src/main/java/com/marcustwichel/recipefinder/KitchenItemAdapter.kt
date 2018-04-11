package com.marcustwichel.recipefinder

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
import com.marcustwichel.recipefinder.model.KitchenItemList



class KitchenItemAdapter() : RecyclerView.Adapter<KitchenItemAdapter.KitchenItemViewHolder>(), Serializable{

    val TAG = "KitchenItemAdapter"

    var mDB : FirebaseFirestore  = FirebaseFirestore.getInstance()
    var mAuth : FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var items : ArrayList<String>
    var deletedItem : String = ""
    private var deletedIndex: Int = 0

    var workingDocument = mDB.collection("kitchens").document(mAuth.currentUser!!.uid)

    init {
        items = ArrayList()
        if (mAuth.currentUser != null) {
            workingDocument.addSnapshotListener(EventListener() { documentSnapshot, exception ->
                if (documentSnapshot.exists()) {
                    var oldSize = items.size
                    items = documentSnapshot.get("items") as ArrayList<String>
                    if (oldSize == 0) {
                        notifyDataSetChanged()
                    } else if (oldSize > items.size) {
                        //itemRemoved
                        notifyDataSetChanged()
                    } else if (oldSize < items.size) {
                        //item added
                        notifyItemInserted(0)
                    }
                    Log.d(TAG, "data changed")
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitchenItemViewHolder {
        return KitchenItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.kitchen_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: KitchenItemViewHolder, position: Int) {
        holder?.itemName?.setText(items?.get(position))
    }

    fun removeItem(position: Int) {
        deletedItem = items.get(position)
        deletedIndex = position
        items?.removeAt(position)
        var data= mutableMapOf<String, MutableList<String>>()
        data["items"] = items
        workingDocument.set(data as MutableMap<String, Any>)
        notifyItemRemoved(position)
    }

    fun addItem(item: String) {
        items?.add(0, toTitleCase(item))
        var data= mutableMapOf<String, MutableList<String>>()
        data["items"] = items
        workingDocument.set(data as MutableMap<String, Any>)
        notifyItemInserted(0)
    }

    fun addItem(item: String, pos : Int) {
        items?.add(pos, toTitleCase(item))
        var data= mutableMapOf<String, MutableList<String>>()
        data["items"] = items
        workingDocument.set(data as MutableMap<String, Any>)
        notifyItemInserted(pos)
    }

    fun restoreLastItem(){
        addItem(deletedItem, deletedIndex)
    }

    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
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