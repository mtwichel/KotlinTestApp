package com.marcustwichel.recipefinder.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.adapters.MainAdapter
import com.marcustwichel.recipefinder.adapters.RecyclerKitchenItemTouchHelper
import com.marcustwichel.recipefinder.adapters.RecyclerSavedItemTouchHelper
import com.marcustwichel.recipefinder.adapters.SavedRecipesAdapter
import com.marcustwichel.recipefinder.recipefinder.api.RecipeRetriver
import com.marcustwichel.recipefinder.recipefinder.model.Recipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class SavedRecipesActivity() : AppCompatActivity(), View.OnClickListener, RecyclerSavedItemTouchHelper.SavedRecipeSwipeListener, Serializable {

    val TAG = "SavedRecipesActivity"

    val mAuth = FirebaseAuth.getInstance()
    val mDB = FirebaseFirestore.getInstance()
    val retriver = RecipeRetriver()
    var savedRecipes = ArrayList<Recipe?>()
    var savedRecipeIds = ArrayList<Int>()

    lateinit var recyclerView : RecyclerView
    lateinit var mRecipeAdapter : SavedRecipesAdapter

    constructor(parcel: android.os.Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_recipes)

        mDB.collection("savedRecipes").document(mAuth.currentUser!!.uid).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            var doneCheck = 0
            savedRecipeIds = documentSnapshot?.data?.get("recipes") as ArrayList<Int>
            savedRecipeIds.sort()
            savedRecipes = ArrayList()
            val callback = object : Callback<Recipe> {
                override fun onFailure(call: Call<Recipe>?, t: Throwable?) {
                    Log.w(TAG, "falure", t)
                }

                override fun onResponse(call: Call<Recipe>?, response: Response<Recipe>?) {
                    if(response != null){
                        savedRecipes.add(response.body())
                        doneCheck += 1
                        if(doneCheck == savedRecipeIds.size){
                            //all done
                            recyclerView = findViewById(R.id.saved_recipes_recyclerView) as RecyclerView
                            mRecipeAdapter = SavedRecipesAdapter(savedRecipes, this@SavedRecipesActivity)
                            recyclerView.layoutManager = LinearLayoutManager(this@SavedRecipesActivity)
                            recyclerView.adapter = mRecipeAdapter


                            val itemTouchHelperCallback = RecyclerSavedItemTouchHelper(0, ItemTouchHelper.LEFT, this@SavedRecipesActivity)
                            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
                        }
                    }
                }

            }
            savedRecipeIds.forEach{
                currentId ->
                retriver.getRecipeById(callback, currentId)
            }


        }
    }

    override fun onClick(view: View?) {
        val intent = Intent(view?.context, DetailActivity::class.java)
        val holder = view?.tag as SavedRecipesAdapter.RecipeViewHolder
        intent.putExtra(DetailActivity.RECIPE,
                mRecipeAdapter?.getRecipe(holder.adapterPosition))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view,
                mRecipeAdapter?.getRecipe(holder.adapterPosition)?.id.toString())
        startActivity(intent, options.toBundle())
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int?) {
        savedRecipeIds.remove(savedRecipeIds[position!!])
        mDB.collection("savedRecipes").document(mAuth.currentUser!!.uid).update("recipes", savedRecipeIds)
    }
}
