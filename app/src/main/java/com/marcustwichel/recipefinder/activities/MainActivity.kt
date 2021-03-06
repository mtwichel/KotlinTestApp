package com.marcustwichel.recipefinder.activities

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
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.marcustwichel.recipefinder.R
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.view.MenuItemCompat





class MainActivity : AppCompatActivity(),
        SearchFragment.OnFragmentInteractionListener,
        KitchenFragment.OnFragmentInteractionListener,
        ListFragment.OnFragmentInteractionListener{

    val TAG : String = "MainActivity"

    lateinit var mAuth : FirebaseAuth
    lateinit var mDB : FirebaseFirestore
    lateinit var toolbar : Toolbar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar  = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseFirestore.getInstance()


        val bottomNavigationView = findViewById(R.id.bottom_nav) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(
                BottomNavigationView.OnNavigationItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.action_bottombaritem_search -> {
                            toolbar.title = "Search"
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_holder, SearchFragment() as Fragment)
                                    .commit()
                        }
                        R.id.action_bottombaritem_kitchen -> {
                            toolbar.title = "Kitchen"
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_holder, KitchenFragment() as Fragment)
                                    .commit()
                        }
                        R.id.action_bottombaritem_list ->{
                            toolbar.title = "Grocery List"
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_holder, ListFragment() as Fragment)
                                    .commit()
                        }


                    }
                    true
                })
        //set first screen to search
        supportActionBar?.setTitle("Search")
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frag_holder, SearchFragment() as Fragment)
                .commit()

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


        when(id){
            R.id.action_sign_out -> {
                signOut()
                return true
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Settings not implemented yet", Toast.LENGTH_LONG).show()
            }
            R.id.action_view_saved -> {
                var intent = Intent(this, SavedRecipesActivity::class.java)
                startActivity(intent)
            }

        }
        return false

    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun signOut(){
        val intent = Intent(this@MainActivity, IntroActivity::class.java)
        intent.putExtra(IntroActivity.FROM, IntroActivity.SIGN_OUT)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser == null){


        }
    }


}
