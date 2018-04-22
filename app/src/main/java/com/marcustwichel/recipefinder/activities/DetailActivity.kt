package com.marcustwichel.recipefinder.activities

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.adapters.DetailIngAdapter
import com.marcustwichel.recipefinder.adapters.DetailStepsAdapter
import com.marcustwichel.recipefinder.recipefinder.api.RecipeRetriver
import com.marcustwichel.recipefinder.recipefinder.model.*
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "DetailActivity"

    lateinit var mAuth : FirebaseAuth
    lateinit var mDB : FirebaseFirestore
    lateinit var kitchenList : ArrayList<String>

    lateinit var stepsRecyclerView: RecyclerView
    lateinit var ingRecyclerView: RecyclerView
    lateinit var progressBar : ProgressBar

    lateinit var loadingSnackbar : Snackbar

    var ings : List<Ingredient>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //Add back button
        val detailToolbar = findViewById(R.id.detail_toolbar) as Toolbar
        setSupportActionBar(detailToolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);


        val recipe = intent.getSerializableExtra(RECIPE) as Recipe?

        kitchenList = ArrayList()
        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseFirestore.getInstance()
        mDB.collection("kitchens").document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
            task ->
            kitchenList = task.result.data?.get("items") as ArrayList<String>
            getRecipe(recipe?.id!!)
        }


        //Add Title
        val collapsing = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsing.title = " "
        val recipeTitle = findViewById(R.id.recipe_title) as TextView
        recipeTitle.text = recipe?.title


        //Add Image
        val image = findViewById(R.id.detail_image) as ImageView
        image.transitionName = recipe?.id.toString()
        postponeEnterTransition()
        recipe?.image.let{
            Glide.with(this).load(recipe?.image).listener(object : RequestListener<String, GlideDrawable> {
                override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            }).into(image)
        }

    }

    fun getRecipe(id : Int){
        var retriever = RecipeRetriver()
        val stepsCallback = object : Callback<List<RecipeStepsResult>> {
            override fun onFailure(call: Call<List<RecipeStepsResult>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<List<RecipeStepsResult>>?, response: Response<List<RecipeStepsResult>>?) {
                response?.isSuccessful.let {
                    val recipeStepsResult = response?.body()
                    if(recipeStepsResult?.size!=0) {
                        val steps = recipeStepsResult?.get(0)?.steps
                        displaySteps(steps!!)
                        progressBar.visibility = View.INVISIBLE
                    }else{
                        Snackbar.make(this@DetailActivity.detail_toolbar, "No Steps Found", Snackbar.LENGTH_LONG).show()
                    }
                }
            }

        }

        val ingsCallback = object : Callback<RecipeIngResult> {
            override fun onFailure(call: Call<RecipeIngResult>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<RecipeIngResult>?, response: Response<RecipeIngResult>?) {
                response?.isSuccessful.let {
                    val recipeIngResult = response?.body()
                    ings = recipeIngResult?.extendedIngredients
                    displayIng(ings!!)
                }
            }

        }

        progressBar = findViewById(R.id.loading_bar) as ProgressBar
        progressBar.visibility = View.VISIBLE
        retriever.getRecipeStepsById(stepsCallback, id)
        retriever.getRecipeIngById(ingsCallback, id)
    }

    companion object {
        val RECIPE = "RECIPE"
    }

    fun displaySteps(steps : List<Step>){
        stepsRecyclerView = findViewById(R.id.steps_recycler_view) as RecyclerView

        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = DetailStepsAdapter(steps)
    }

    fun displayIng(ings : List<Ingredient>){
        ingRecyclerView = findViewById(R.id.ing_recycler_view) as RecyclerView
        ingRecyclerView.layoutManager = LinearLayoutManager(this)
        ingRecyclerView.adapter = DetailIngAdapter(ings, kitchenList, this)
    }

    override fun onClick(view: View?) {
        val itemToBeAdded = toTitleCase(ings!![ingRecyclerView.getChildAdapterPosition(view)].name)
        kitchenList.add(0, itemToBeAdded)
        mDB.collection("kitchens").document(mAuth.currentUser!!.uid).update("items", kitchenList)
        loadingSnackbar = Snackbar.make(stepsRecyclerView,
                        itemToBeAdded + " added to kitchen", Snackbar.LENGTH_LONG)

        loadingSnackbar.setAction("UNDO", View.OnClickListener {
            kitchenList.remove(itemToBeAdded)
            mDB.collection("kitchens").document(mAuth.currentUser!!.uid).update("items", kitchenList)
        })
        loadingSnackbar.setActionTextColor(Color.YELLOW)
        loadingSnackbar.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }
}
