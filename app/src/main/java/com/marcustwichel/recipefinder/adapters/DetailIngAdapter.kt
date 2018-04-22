package com.marcustwichel.recipefinder.adapters

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.recipefinder.model.Ingredient

/**
 * Created by mtwichel on 3/16/18.
 */
class DetailIngAdapter(var ings : List<Ingredient>, var kitchenList : ArrayList<String>, val clickListener : View.OnClickListener) :
        RecyclerView.Adapter<DetailIngAdapter.StepViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        return StepViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.ing_item, parent, false))
    }

    override fun getItemCount(): Int {
        return ings.size
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val currentIng = ings[position]

        if(kitchenList.contains(toTitleCase(currentIng.name))){
            holder.ingText?.setTypeface(holder.ingText?.getTypeface(), Typeface.BOLD)
        }else{
            holder.ingText?.setTypeface(holder.ingText?.getTypeface(), Typeface.NORMAL)
        }

        var currentAmount = ""
        if(currentIng.amount - currentIng.amount.toInt() == 0.0 ){
            //is whole
            currentAmount = currentIng.amount.toInt().toString()
        }else{
            var decimalPart = currentIng.amount - currentIng.amount.toInt()
            var intPart = currentIng.amount.toInt().toString()
            if(intPart.equals("0") ){
                intPart = ""
            }
            if(decimalPart - 0.25 < 0.00001){
                currentAmount = intPart + "¼"
            }else if(decimalPart - 0.3333333 < 0.00001){
                currentAmount = intPart + "⅓"
            }else if(decimalPart - 0.5 < 0.00001){
                currentAmount = intPart + "½"
            }else if(decimalPart - 0.6666666 < 0.00001){
                currentAmount = intPart + "⅔"
            }else if(decimalPart - 0.75 < 0.00001){
                currentAmount = intPart + "¾"
            }else {
                currentAmount = currentIng.amount.toString()
            }
        }

        holder?.ingText?.text = currentAmount + " " + currentIng.unit + " " + currentIng.name


        Glide.with(holder?.ingText?.context!!)
                    .load(currentIng.image)
                    .into(holder?.ingImg)

    }

    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }


    inner class StepViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var ingText : TextView?
        var ingImg : ImageView?

        init {
            if(clickListener != null){
                itemView.setOnClickListener(clickListener)
            }
            itemView.tag = this
            ingText = itemView.findViewById(R.id.ing_text) as TextView
            ingImg = itemView.findViewById(R.id.ing_img) as ImageView

        }
    }
}