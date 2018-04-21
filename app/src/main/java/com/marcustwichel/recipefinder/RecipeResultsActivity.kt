package com.marcustwichel.recipefinder

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.marcustwichel.recipefinder.model.RecipeSearchResult
import com.marcustwichel.recipefinder.recipefinder.api.RecipeRetriver
import com.marcustwichel.recipefinder.recipefinder.model.Recipe

import kotlinx.android.synthetic.main.activity_recipe_results.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.support.v4.app.ActivityOptionsCompat



class RecipeResultsActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "RecipeResultsActivity"

    lateinit var recyclerView : RecyclerView
    var recipes : List<Recipe>? = null
    var mainAdapter : MainAdapter? = null
    var seachingSnackbar : Snackbar? = null
    var retriever = RecipeRetriver()

    var searchString : String? = null
    var cuisine : String? = null
    var type : String? = null
    var queryString : String? = null
    var ranking : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_results)
        setSupportActionBar(toolbar)

        toolbar.setTitle("Search Results")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recycler_view_results)
        recyclerView.layoutManager = LinearLayoutManager(this)
        retriever = RecipeRetriver()

        searchString = intent.getStringExtra("searchString")
        cuisine = intent.getStringExtra("cuisine")
        type = intent.getStringExtra("type")
        queryString = intent.getStringExtra("queryString")
        ranking = intent.getIntExtra("ranking", 0)

        searchRecipies()
    }

    override fun onClick(view: View?) {
        val intent = Intent(view?.context, DetailActivity::class.java)
        val holder = view?.tag as MainAdapter.RecipeViewHolder
        intent.putExtra(DetailActivity.RECIPE,
                mainAdapter?.getRecipe(holder.adapterPosition))
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view,
                mainAdapter?.getRecipe(holder.adapterPosition)?.id.toString())
        startActivity(intent, options.toBundle())
    }

    private fun searchRecipies() {

        val callback = object : Callback<RecipeSearchResult> {
            override fun onFailure(call: Call<RecipeSearchResult>?, t: Throwable?) {
                Log.d(TAG, "response not successful", t)
                seachingSnackbar?.dismiss()
            }

            override fun onResponse(call: Call<RecipeSearchResult>?, response: Response<RecipeSearchResult>?) {
                response?.isSuccessful.let {
                    Log.d(TAG, "response successful")
                    this@RecipeResultsActivity.recipes = response?.body()?.results

                    this@RecipeResultsActivity.mainAdapter = MainAdapter(this@RecipeResultsActivity.recipes!!,
                            this@RecipeResultsActivity)
                    this@RecipeResultsActivity.recyclerView.adapter = this@RecipeResultsActivity.mainAdapter

                    seachingSnackbar?.dismiss()
                }
            }

        }
        seachingSnackbar = Snackbar.make(recyclerView,
                "Searching", Snackbar.LENGTH_INDEFINITE)

        retriever.getRecipes(callback, searchString, cuisine, type, queryString, ranking)
        seachingSnackbar?.show()


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



}
