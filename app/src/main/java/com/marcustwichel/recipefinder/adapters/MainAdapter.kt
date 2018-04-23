package com.marcustwichel.recipefinder.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.recipefinder.model.Recipe

/**
 * Created by marcus.twichel on 3/14/18.
 */
class MainAdapter(var recipes: List<Recipe>,
                  var clickListener: View.OnClickListener) :
        RecyclerView.Adapter<MainAdapter.RecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.recipe_item, parent, false))
    }

    fun getRecipe(adapterPosition: Int) : Recipe{
        return recipes[adapterPosition]
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipie = recipes[position]
        holder?.title?.text = recipie.title
        if(recipie.image.isNotEmpty()){
            Glide.with(holder?.title?.context!!)
                    .load(recipie.image)
                    .into(holder?.recipe_photo)
        }
        holder.recipe_photo.transitionName = (recipie.id.toString())
        holder?.usedMissing?.text =
                "Used: " +
                recipie.usedIngredientCount.toString() +
                " Missing: " +
                recipie.missedIngredientCount.toString()

    }

    inner class RecipeViewHolder(itemView : View) : RecyclerView.ViewHolder (itemView){
        var title : TextView
        var recipe_photo : ImageView
        var usedMissing : TextView

        init {
            if (clickListener != null){
                itemView.setOnClickListener(clickListener)
            }
            itemView.tag = this
            title = itemView.findViewById(R.id.title) as TextView
            recipe_photo = itemView.findViewById(R.id.recipe_photo) as ImageView
            usedMissing = itemView.findViewById(R.id.used_missing) as TextView
        }
    }
}