package com.marcustwichel.recipefinder

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.recipefinder.fragments.ListFragment
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener




/**
 * Created by mtwichel on 4/11/18.
 */
class ListItemAdapter() :  RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>(), Serializable {

    val TAG = "ListItemAdapter"

    var mDB : FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var items : ArrayList<String>
    var values : ArrayList<Boolean>
    var kitchenItems : ArrayList<String>


    var workingDocument = mDB.collection("groceryLists").document(mAuth.currentUser!!.uid)
    var kitchenDocument = mDB.collection("kitchens").document(mAuth.currentUser!!.uid)

    init{
        items = ArrayList()
        values = ArrayList()
        kitchenItems = ArrayList()


        if (mAuth.currentUser != null) {
            workingDocument.addSnapshotListener(EventListener() { documentSnapshot, exception ->
                if (documentSnapshot.exists()) {
                    var oldSize = items.size
                    items = documentSnapshot.get("items") as ArrayList<String>
                    values = documentSnapshot.get("values") as ArrayList<Boolean>

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

        if (mAuth.currentUser != null) {
            kitchenDocument.addSnapshotListener(EventListener(){ documentSnapshot, exception->
                if(documentSnapshot.exists()){
                    kitchenItems = documentSnapshot.get("items") as ArrayList<String>
                }
            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        return ListItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val checkbox = holder.checkbox
        holder?.checkbox?.text = items[position]
        if(values[position]){
            checkbox?.setPaintFlags(checkbox?.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG);
            checkbox?.setChecked(true)
        }
        holder.checkbox?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener(){
            buttonView, isChecked ->
            if(isChecked){
                checkbox?.setPaintFlags(checkbox?.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG);
                checkItem(holder.adapterPosition)
            }else{
                uncheckItem(holder.adapterPosition)
                checkbox?.setPaintFlags(0)
            }

        })

    }



    fun removeItem(position: Int) {
        items.removeAt(position)
        values.removeAt(position)
        var data= mutableMapOf<String, ArrayList<Any>>()
        data["items"] = items as ArrayList<Any>
        data["values"] = values as ArrayList<Any>
        workingDocument.set(data as MutableMap<String, Any>)
        notifyItemRemoved(position)
    }


    fun addItem(item: String) {
        items.add(0, toTitleCase(item))
        values.add(0, false)
        var data= mutableMapOf<String, ArrayList<Any>>()
        data["items"] = items as ArrayList<Any>
        data["values"] = values as ArrayList<Any>
        workingDocument.set(data as MutableMap<String, Any>)
        notifyItemInserted(0)
    }

    fun checkItem(position: Int){
        values[position] = true
        var data= mutableMapOf<String, ArrayList<Any>>()
        data["items"] = items as ArrayList<Any>
        data["values"] = values as ArrayList<Any>
        workingDocument.set(data as MutableMap<String, Any>)
    }

    private fun uncheckItem(position: Int) {
        values[position] = false
        var data= mutableMapOf<String, ArrayList<Any>>()
        data["items"] = items as ArrayList<Any>
        data["values"] = values as ArrayList<Any>
        workingDocument.set(data as MutableMap<String, Any>)
    }



    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }

    fun getItemName(adapterPosition: Int): String {
        return items.get(adapterPosition)
    }

    fun moveCheckedToKitchen() {
        Log.i(TAG, "Value size" + values.size)
        for(i in 0 until values.size){
            if(values[i]){
                //checked
                kitchenItems?.add(items[i])
            }
        }
        var newItems = ArrayList<String>()
        var newValues = ArrayList<Boolean>()
        values.forEachIndexed() { index, current ->
            if(!current){
                newItems.add(items[index])
                newValues.add(false)
            }
        }

        items = newItems
        values = newValues

        var data= mutableMapOf<String, ArrayList<Any>>()
        data["items"] = items as ArrayList<Any>
        data["values"] = values as ArrayList<Any>
        workingDocument.set(data as MutableMap<String, Any>)

        data= mutableMapOf<String, ArrayList<Any>>()
        data["items"] = kitchenItems as ArrayList<Any>
        kitchenDocument.set(data as MutableMap<String, Any>)

        notifyDataSetChanged()
    }

    inner class ListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var checkbox : CheckBox?

        var viewBackground : RelativeLayout?
        var viewForeground : LinearLayout?

        init{
            checkbox = itemView.findViewById(R.id.checkbox)

            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)
        }
    }


}