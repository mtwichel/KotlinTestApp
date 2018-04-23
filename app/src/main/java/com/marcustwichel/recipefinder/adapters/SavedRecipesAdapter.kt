package com.marcustwichel.recipefinder.adapters

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
import com.marcustwichel.recipefinder.recipefinder.api.RecipeRetriver
import com.marcustwichel.recipefinder.recipefinder.model.Recipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by mtwichel on 4/22/18.
 */
class SavedRecipesAdapter(val recipes : ArrayList<Recipe?>, val clickListener: View.OnClickListener) : RecyclerView.Adapter<SavedRecipesAdapter.RecipeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRecipesAdapter.RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.saved_recipe_item, parent, false))    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: SavedRecipesAdapter.RecipeViewHolder, position: Int) {
        val recipie = recipes[position]
        holder?.title?.text = recipie?.title
        if(recipie!!.image.isNotEmpty()){
            Glide.with(holder?.title?.context!!)
                    .load(recipie.image)
                    .into(holder?.recipe_photo)
        }
        holder.recipe_photo.transitionName = (recipie.id.toString())
    }

    inner class RecipeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title : TextView
        var recipe_photo : ImageView

        init {
            if (clickListener != null){
                itemView.setOnClickListener(clickListener)
            }
            itemView.tag = this
            title = itemView.findViewById(R.id.title) as TextView
            recipe_photo = itemView.findViewById(R.id.recipe_photo) as ImageView
        }
    }

    fun getRecipe(adapterPosition: Int): Recipe? {
        return recipes.get(adapterPosition)
    }
}