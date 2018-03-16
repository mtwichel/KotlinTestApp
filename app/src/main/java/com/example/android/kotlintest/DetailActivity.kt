package com.example.android.kotlintest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.view.ViewCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.android.kotlintest.api.RecipieRetriver
import com.example.android.kotlintest.model.Recipe
import com.example.android.kotlintest.model.RecipeSteps
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    var recipeSteps : RecipeSteps? = null;
    var stepsText : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val recipe = intent.getSerializableExtra(RECIPE) as Recipe?


        //Add Steps
        val stepsText = findViewById(R.id.instruction_text) as TextView
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
        val callback = object : Callback<List<RecipeSteps>> {
            override fun onFailure(call: Call<List<RecipeSteps>>?, t: Throwable?) {
                Log.e("MainActivity", "Problems Calling API", t)
            }

            override fun onResponse(call: Call<List<RecipeSteps>>?, response: Response<List<RecipeSteps>>?) {
                response?.isSuccessful.let {
                    Log.i("MainActivity", "API Call successful")
                    this@DetailActivity.recipeSteps = response?.body()?.get(0)
                    var steps = recipeSteps?.steps
                    stepsText?.text = steps?.get(0)?.step

                    //TODO Finish
                }
            }

        }

        retriever.getRecipeById(callback, id)
    }

    companion object {
        val RECIPE = "RECIPE"
    }
}
