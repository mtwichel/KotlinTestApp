package com.marcustwichel.recipefinder.activities

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ShareActionProvider
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
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
    lateinit var savedRecipies : ArrayList<Int>
    lateinit var recipe: Recipe

    lateinit var stepsRecyclerView: RecyclerView
    lateinit var ingRecyclerView: RecyclerView
    lateinit var ingAdapter : DetailIngAdapter
    lateinit var progressBar : ProgressBar
    lateinit var sourceText : TextView
    lateinit var readyInMinutes : TextView


    lateinit var loadingSnackbar : Snackbar

    var ings : List<Ingredient>? = null

    lateinit var mShareActionProvider: ShareActionProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)

        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseFirestore.getInstance()
        recipe = intent.getSerializableExtra(RECIPE) as Recipe

        //Add back button
        val detailToolbar = findViewById(R.id.detail_toolbar) as Toolbar
        setSupportActionBar(detailToolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        var saveFab = findViewById(R.id.fab_save_recipe) as FloatingActionButton
        sourceText = findViewById(R.id.source_text)
        readyInMinutes = findViewById(R.id.ready_in_minutes)


        mDB.collection("savedRecipes").document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
            task ->
            if(task.result.data?.get("recipes") == null){
                var data = HashMap<String, ArrayList<Int>>()
                data.put("recipes", ArrayList<Int>())
                mDB.collection("savedRecipes").document(mAuth.currentUser!!.uid).set(data as MutableMap<String, Any>)
                savedRecipies = ArrayList<Int>()
            }else{
                savedRecipies = task.result.data?.get("recipes") as ArrayList<Int>
                saveFab.setOnClickListener {
                    Log.d(TAG, "" + savedRecipies.toString() + " | " +recipe.id)
                    if(listContains(recipe.id, savedRecipies)){
                        Log.d(TAG, "true")
                        Snackbar.make(ingRecyclerView, "Recipe Already Saved", Snackbar.LENGTH_SHORT).show()
                    }else {
                        Log.d(TAG, "false")
                        savedRecipies.add(recipe.id)
                        mDB.collection("savedRecipes").document(mAuth.currentUser!!.uid).update("recipes", savedRecipies)
                        Snackbar.make(ingRecyclerView, "Recipe Saved!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }


        kitchenList = ArrayList()
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
                        val steps = recipeStepsResult?.get(0)?.steps as ArrayList<Step>
                        displaySteps(steps!!)
                        progressBar.visibility = View.INVISIBLE
                    }else{
                        Snackbar.make(this@DetailActivity.detail_toolbar, "No Steps Found", Snackbar.LENGTH_LONG).show()
                    }
                }
            }

        }

        val recipeIngCallback = object : Callback<RecipeIngResult> {
            override fun onFailure(call: Call<RecipeIngResult>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<RecipeIngResult>?, response: Response<RecipeIngResult>?) {
                response?.isSuccessful.let {
                    val recipeIngResult = response?.body()
                    recipe.sourceUrl = recipeIngResult!!.sourceUrl
                    if(recipeIngResult!!.sourceName != null){
                        sourceText.text = "From: " + recipeIngResult!!.sourceName
                    }
                    if(recipeIngResult!!.readyInMinutes != null){
                        readyInMinutes.text = "Ready in " + recipeIngResult!!.readyInMinutes + " minutes!"
                    }


                    ings = recipeIngResult.extendedIngredients
                    displayIng(ings!!)
                }
            }

        }

        progressBar = findViewById(R.id.loading_bar) as ProgressBar
        progressBar.visibility = View.VISIBLE
        retriever.getRecipeStepsById(stepsCallback, id)
        retriever.getRecipeIngById(recipeIngCallback, id)
    }

    companion object {
        val RECIPE = "RECIPE"
    }

    fun displaySteps(steps : ArrayList<Step>){
        stepsRecyclerView = findViewById(R.id.steps_recycler_view) as RecyclerView

        stepsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.adapter = DetailStepsAdapter(steps)
    }

    fun displayIng(ings : List<Ingredient>){
        ingRecyclerView = findViewById(R.id.ing_recycler_view) as RecyclerView
        ingRecyclerView.layoutManager = LinearLayoutManager(this)
        ingAdapter = DetailIngAdapter(ings, kitchenList, this)
        ingRecyclerView.adapter = ingAdapter
    }

    override fun onClick(view: View?) {
        val itemToBeAdded = toTitleCase(ings!![ingRecyclerView.getChildAdapterPosition(view)].name)
        kitchenList.add(0, itemToBeAdded)
        ingAdapter.kitchenList.add(0, itemToBeAdded)
        mDB.collection("kitchens").document(mAuth.currentUser!!.uid).update("items", kitchenList).addOnSuccessListener {
            ingAdapter.notifyDataSetChanged()
        }
        loadingSnackbar = Snackbar.make(stepsRecyclerView,
                        itemToBeAdded + " added to kitchen", Snackbar.LENGTH_LONG)

        loadingSnackbar.setAction("UNDO", View.OnClickListener {
            kitchenList.remove(itemToBeAdded)
            ingAdapter.kitchenList.remove(itemToBeAdded)
            mDB.collection("kitchens").document(mAuth.currentUser!!.uid).update("items", kitchenList)
        })
        loadingSnackbar.setActionTextColor(Color.YELLOW)
        loadingSnackbar.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "" + item.itemId + " | "+R.id.action_share)
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_share -> {
                Log.i(TAG, "Share Started" + recipe.sourceUrl)
                var sendIntent = Intent()
                sendIntent.setAction(Intent.ACTION_SEND)
                sendIntent.putExtra(Intent.EXTRA_TEXT, recipe.sourceUrl);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Send To"))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detail, menu)
        // Locate MenuItem with ShareActionProvider
//        val item = menu.findItem(R.id.action_share)
//        mShareActionProvider = MenuItemCompat.getActionProvider(item) as ShareActionProvider
        return true
    }


    private fun toTitleCase(string :String) : String{
        return when (string.length) {
            0 -> ""
            1 -> string.toUpperCase()
            else -> string[0].toUpperCase() + string.substring(1)
        }
    }
    private fun listContains(needle : Int, haystack : ArrayList<Int>) : Boolean{
        haystack.forEach { current ->
            if(current == needle){
                return true
            }
        }
        return false
    }
}
