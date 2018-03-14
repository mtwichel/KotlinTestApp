package com.example.android.kotlintest

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import com.example.android.kotlintest.api.RecipieRetriver
import com.example.android.kotlintest.model.Recipie
import com.example.android.kotlintest.model.RecipieList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var recipies : List<Recipie>? = null
    var mainAdapter : MainAdapter? = null
    lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        var retriever = RecipieRetriver()
        val callback = object : Callback<RecipieList> {
            override fun onFailure(call: Call<RecipieList>?, t: Throwable?) {
                Log.e("MainActivity", "Problems Calling API", t)
            }

            override fun onResponse(call: Call<RecipieList>?, response: Response<RecipieList>?) {
                response?.isSuccessful.let {
                    this@MainActivity.recipies = response?.body()?.recipies
                    mainAdapter = MainAdapter(this@MainActivity.recipies!!,
                            this@MainActivity)
                    recyclerView.adapter = mainAdapter
                }
            }

        }

        retriever.getRecipies(callback)


        //        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //        fab.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        //            }
        //        });
    }

    override fun onClick(view: View?) {

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
