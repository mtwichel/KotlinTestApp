package com.marcustwichel.recipefinder

import com.marcustwichel.recipefinder.recipefinder.fragments.ListFragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.marcustwichel.recipefinder.recipefinder.fragments.KitchenFragment
import com.marcustwichel.recipefinder.recipefinder.fragments.SearchFragment


class MainActivity : AppCompatActivity(),
        SearchFragment.OnFragmentInteractionListener,
        KitchenFragment.OnFragmentInteractionListener,
        ListFragment.OnFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)



        //set first screen to search
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_holder, SearchFragment() as Fragment)
                .commit()

        val bottomNavigationView = findViewById(R.id.bottom_nav) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.action_bottombaritem_search -> {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_holder, SearchFragment() as Fragment)
                                    .commit()
                        }
                        R.id.action_bottombaritem_kitchen -> {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_holder, KitchenFragment() as Fragment)
                                    .commit()
                        }
                        R.id.action_bottombaritem_list ->{
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_holder, ListFragment() as Fragment)
                                    .commit()
                        }


                    }
                    true
                })
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
