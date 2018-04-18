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
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.marcustwichel.recipefinder.model.ListItem




class ListItemAdapter(val recyclerView: RecyclerView) :  RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder>(), Serializable {


    val TAG = "ListItemAdapter"

    var mDB : FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var items : ArrayList<ListItem?>
    var kitchenItems : ArrayList<String>



    var workingDocument = mDB.collection("groceryLists").document(mAuth.currentUser!!.uid)
    var kitchenDocument = mDB.collection("kitchens").document(mAuth.currentUser!!.uid)

    init{
        items = ArrayList()
        kitchenItems = ArrayList()


        if (mAuth.currentUser != null) {
            workingDocument.addSnapshotListener(EventListener() { documentSnapshot, exception ->

                if(mAuth.currentUser!= null){
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        workingDocument.collection("items").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for(document in task.result.documents){
                                    items.add(document.toObject(ListItem::class.java))
                                }
                                notifyDataSetChanged()
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.exception)
                            }
                        }
                    }
                }
            })
        }

        if (mAuth.currentUser != null) {
            kitchenDocument.addSnapshotListener(EventListener(){ documentSnapshot, exception->
                if(documentSnapshot != null && documentSnapshot.exists()){
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
        holder?.checkbox?.text = items[position]?.string
        checkbox?.isChecked = items[position]!!.checked
    }



    fun removeItem(position: Int) {
        workingDocument.collection("items").document(items[position]?.id.toString()).delete()
    }


    fun addItem(item: String) {
        var map = HashMap<String, Any>()
        var currentId : Int = -1
        workingDocument.get().addOnSuccessListener { task ->
            currentId = task.data?.get("id") as Int
        }
        map.put("id", currentId)
        map.put("string", item)
        map.put("checked", false)




    }

    fun checkItem(position: Int){

    }

    private fun uncheckItem(position: Int) {

    }



    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
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

