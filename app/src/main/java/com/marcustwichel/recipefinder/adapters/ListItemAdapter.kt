package com.marcustwichel.recipefinder.adapters

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


class ListItemAdapter(val recyclerView: RecyclerView) :  RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>(), Serializable {


    val TAG = "ListItemAdapter"

    var mDB : FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var items : ArrayList<ListItem?>
    var kitchenItems : ArrayList<String>
    var currentId : Int? = null
    var oldSize : Int = 0
    var removePos : Int? = null

    var workingDocument = mDB.collection("groceryLists").document(mAuth.currentUser!!.uid)
    var kitchenDocument = mDB.collection("kitchens").document(mAuth.currentUser!!.uid)

    init{
        items = ArrayList()
        kitchenItems = ArrayList()


        if (mAuth.currentUser != null) {
            workingDocument.collection("items").addSnapshotListener(EventListener() { collectionSnapshot, exception ->
                Log.d(TAG, "Collection Changed")
                oldSize = items.size

                items = ArrayList()
                collectionSnapshot!!.documents.forEachIndexed { index, documentSnapshot ->
                    items.add(documentSnapshot.toObject(ListItem::class.java))
                }

                Collections.sort(items)

                collectionSnapshot!!.documents.forEachIndexed{ index: Int, documentSnapshot: DocumentSnapshot? ->
                    documentSnapshot!!.reference.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        notifyItemChanged(index)
                    }
                }
                when {
                    oldSize > items.size -> {
                        notifyDataSetChanged()
                    }
                    oldSize < items.size -> {
                        notifyItemInserted(0)
                        recyclerView.scrollToPosition(0)
                    }
                    else -> notifyDataSetChanged()
                }
            })
        }

        getCurrentIdNum()

        if (mAuth.currentUser != null) {
            kitchenDocument.addSnapshotListener(EventListener(){ documentSnapshot, exception->
                if(documentSnapshot != null && documentSnapshot.exists()){
                    kitchenItems = documentSnapshot.get("items") as ArrayList<String>
                }
            })
        }
    }



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
        }
    }

    private fun toggleListItem(position : Int) {
        workingDocument.collection("items").document(items[position]?.id.toString()).update("checked", items[position]?.checked?.not())
    }


    fun removeItem(position: Int) {
        workingDocument.collection("items").document(items[position]?.id.toString()).delete()
        notifyItemRemoved(position)
    }


    fun addItem(item: String) {
        if(currentId != null) {
            var map = HashMap<String, Any>()
            map.put("id", currentId!!)
            map.put("string", toTitleCase(item))
            map.put("checked", false)
            workingDocument.collection("items").document(currentId.toString()).set(map)
            workingDocument.update("currentId", currentId!! + 1)
        }

    }

    fun checkItem(position: Int){
        workingDocument.collection("items").document(items[position]?.id.toString()).update("checked", true).addOnSuccessListener {
            notifyItemChanged(position)
        }
    }

    private fun uncheckItem(position: Int) {
        workingDocument.collection("items").document(items[position]?.id.toString()).update("checked", false).addOnSuccessListener {
            notifyItemChanged(position)
        }
    }

    fun getCurrentIdNum(){
        workingDocument.get().addOnSuccessListener { task ->
            if (task.data?.get("currentId") == null) {
                currentId = 0
                var idMap = HashMap<String, Int>()
                idMap.put("currentId", 0)
                workingDocument.set(idMap as MutableMap<String, Any>)
            } else {
                currentId = (task.data?.get("currentId") as Long).toInt()
            }
        }
    }


    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }





    fun moveCheckedToKitchen() {
//        Log.i(TAG, "Value size" + values.size)
//        for(i in 0 until values.size){
//            if(values[i]){
//                //checked
//                kitchenItems?.add(items[i])
//            }
//        }
//        var newItems = ArrayList<String>()
//        var newValues = ArrayList<Boolean>()
//        values.forEachIndexed() { index, current ->
//            if(!current){
//                newItems.add(items[index])
//                newValues.add(false)
//            }
//        }
//
//        items = newItems
//        values = newValues
//
//        var data= mutableMapOf<String, ArrayList<Any>>()
//        data["items"] = items as ArrayList<Any>
//        data["values"] = values as ArrayList<Any>
//        workingDocument.set(data as MutableMap<String, Any>)
//
//        data= mutableMapOf<String, ArrayList<Any>>()
//        data["items"] = kitchenItems as ArrayList<Any>
//        kitchenDocument.set(data as MutableMap<String, Any>)
//
//        notifyDataSetChanged()
    }

    inner class ListItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var listText : TextView?

        var viewBackground : RelativeLayout?
        var viewForeground : LinearLayout?

        init{
            listText = itemView.findViewById(R.id.list_text)

            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)
        }
    }




}

