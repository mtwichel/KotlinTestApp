package com.example.android.kotlintest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.android.kotlintest.api.RecipieRetriver
import com.example.android.kotlintest.model.Recipe
import com.example.android.kotlintest.model.RecipeStepsResult
import com.example.android.kotlintest.model.Step
import com.example.android.kotlintest.DetailStepsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //Add back button
        val detailToolbar = findViewById(R.id.detail_toolbar) as Toolbar
        setSupportActionBar(detailToolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);


        val recipe = intent.getSerializableExtra(RECIPE) as Recipe?


        //Add Steps
        getRecipe(recipe?.id!!)


        //Add Title
        val collapsing = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsing.title = recipe?.title



        //Add Image
        val image = findViewById(R.id.detail_image) as ImageView
        recipe?.image.let{
            Glide.with(this).load(recipe?.image).into(image)
        }


    }

    fun getRecipe(id : Int){
        var retriever = RecipieRetriver()
        val callback = object : Callback<List<RecipeStepsResult>> {
            override fun onFailure(call: Call<List<RecipeStepsResult>>?, t: Throwable?) {
                Log.e("DetailActivity", "Problems Calling API", t)
            }

            override fun onResponse(call: Call<List<RecipeStepsResult>>?, response: Response<List<RecipeStepsResult>>?) {
                response?.isSuccessful.let {
                    Log.i("DetailActivity", "API Call successful")
                    val recipeStepsResult = response?.body()?.get(0)
                    if (recipeStepsResult == null){
                        Log.d("DetailActivity", "recipeResult Null")
                    }
                    val steps = recipeStepsResult?.stepsList
                    if (recipeStepsResult?.stepsList == null){
                        Log.d("DetailActivity", "stepsList Null")
                    }
                    displaySteps(steps!!)
                }
            }

        }

        retriever.getRecipeById(callback, id)
    }

    companion object {
        val RECIPE = "RECIPE"
    }

    fun displaySteps(steps : List<Step>){
        recyclerView = findViewById(R.id.steps_recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DetailStepsAdapter(steps)
    }
}
