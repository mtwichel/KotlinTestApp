package com.marcustwichel.recipefinder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.marcustwichel.recipefinder.recipefinder.model.Ingredient

/**
 * Created by mtwichel on 3/16/18.
 */
class DetailIngAdapter(var ings : List<Ingredient>) :
        RecyclerView.Adapter<DetailIngAdapter.StepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        return StepViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.ing_item, parent, false))
    }

    override fun getItemCount(): Int {
        return ings.size
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val currentStep = ings[position]
        if(currentStep.amount != 1.0){
            if(currentStep.amount - currentStep.amount.toInt() == 0.0 ){
                //is whole
                holder?.ingText?.text = "" + currentStep.amount.toInt() + " " + currentStep.unit + " " + currentStep.name;
            }else {
                holder?.ingText?.text = "" + currentStep.amount + " " + currentStep.unit + " " + currentStep.name;
            }
        }else{
            holder?.ingText?.text = "" + currentStep.amount.toInt() + " " + currentStep.unit + " " + currentStep.name;
        }

        if(currentStep.image.isNotEmpty()){
            Glide.with(holder?.ingText?.context!!)
                    .load(currentStep.image)
                    .into(holder?.ingImg)
        }
    }


    inner class StepViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var ingText : TextView?
        var ingImg : ImageView?

        init {

            itemView.tag = this
            ingText = itemView.findViewById(R.id.ing_text) as TextView
            ingImg = itemView.findViewById(R.id.ing_img) as ImageView

        }
    }
}