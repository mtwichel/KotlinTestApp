package com.example.android.kotlintest

import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.android.kotlintest.model.Recipie
import java.text.FieldPosition

/**
 * Created by marcus.twichel on 3/14/18.
 */
class MainAdapter(var recipies: List<Recipie>,
                  var clickListener: View.OnClickListener) :
        RecyclerView.Adapter<MainAdapter.RecipieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecipieViewHolder {
        return RecipieViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.recipie_item, parent, false))
    }

    fun getRecipie(adapterPosition: Int) : Recipie{
        return recipies[adapterPosition]
    }

    override fun getItemCount(): Int {
        return recipies.size
    }

    override fun onBindViewHolder(holder: RecipieViewHolder?, position: Int) {
        val recipie = recipies[position]
        holder?.title?.text = recipie.title
        if(recipie.imgUrl.isNotEmpty()){
            Glide.with(holder?.title?.context)
                    .load(recipie.imgUrl)
                    .into(holder?.recipie_photo)
        }

    }

    inner class RecipieViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var title : TextView
        var recipie_photo : ImageView

        init {
            if (clickListener != null){
                itemView.setOnClickListener(clickListener)
            }
            itemView.tag = this
            title = itemView.findViewById(R.id.title) as TextView
            recipie_photo = itemView.findViewById(R.id.recipie_photo) as ImageView
        }
    }
}