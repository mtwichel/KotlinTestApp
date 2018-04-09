package com.marcustwichel.recipefinder

import android.app.Activity
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
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Intent
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity(),
        SearchFragment.OnFragmentInteractionListener,
        KitchenFragment.OnFragmentInteractionListener,
        ListFragment.OnFragmentInteractionListener{

    val RC_SIGN_IN = 123
    lateinit var mAuth : FirebaseAuth
    lateinit var mDB : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        mDB = FirebaseFirestore.getInstance()

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


        when(id){
            R.id.action_sign_out -> {
                signOut()
                recreate()
                return true
            }

        }
        return false

    }

    private fun signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        Toast.makeText(this@MainActivity, "Signed Out", Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun signIn() {
        val providers = Arrays.asList(
                AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if(currentUser == null){
            signIn()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, "Logged On as " + user?.displayName, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "LOGON FAILED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
