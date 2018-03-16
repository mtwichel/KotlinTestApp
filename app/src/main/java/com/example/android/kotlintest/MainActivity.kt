package com.example.android.kotlintest

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.example.android.kotlintest.api.RecipieRetriver
import com.example.android.kotlintest.model.Recipe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var recipes : List<Recipe>? = null
    var mainAdapter : MainAdapter? = null
    lateinit var recyclerView: RecyclerView
    var seachingSnackbar : Snackbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        seachingSnackbar = Snackbar.make(recyclerView, "Searching", Snackbar.LENGTH_INDEFINITE)

        val searchButton = findViewById(R.id.search_button) as Button
        val searchBar = findViewById(R.id.search_bar) as EditText

        searchButton.setOnClickListener {
            Log.i("MainActivity", "Seaching for Recipies")
            hideKeyboard()
            seachingSnackbar?.show()
            searchRecipies(searchBar.text.toString())
        }




        //        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //        fab.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        //            }
        //        });
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchRecipies(searchString : String) {
        var retriever = RecipieRetriver()
        val callback = object : Callback<List<Recipe>> {
            override fun onFailure(call: Call<List<Recipe>>?, t: Throwable?) {
                Log.e("MainActivity", "Problems Calling API", t)
            }

            override fun onResponse(call: Call<List<Recipe>>?, response: Response<List<Recipe>>?) {
                response?.isSuccessful.let {
                    Log.i("MainActivity", "API Call successful")
                    this@MainActivity.recipes = response?.body()
                    mainAdapter = MainAdapter(this@MainActivity.recipes!!,
                            this@MainActivity)
                    recyclerView.adapter = mainAdapter
                    this@MainActivity.seachingSnackbar?.dismiss()
                }
            }

        }

        retriever.getRecipes(callback, searchString)
    }

    override fun onClick(view: View?) {
        val intent = Intent(this, DetailActivity::class.java)
        val holder = view?.tag as MainAdapter.RecipeViewHolder
        intent.putExtra(DetailActivity.RECIPE,
                mainAdapter?.getRecipe(holder.adapterPosition));
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }
}
