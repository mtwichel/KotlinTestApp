package com.marcustwichel.recipefinder.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.marcustwichel.recipefinder.R
import com.marcustwichel.recipefinder.recipefinder.model.Recipe
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class IntroActivity : AppCompatActivity() {

    val RC_SIGN_IN = 123
    var mAuth : FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_intro)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if(intent != null && intent.hasExtra(FROM) && intent.getIntExtra(FROM, 0) as Int == SIGN_OUT){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(task: Task<Void>) {
                            Toast.makeText(this@IntroActivity, "Signed Out", Toast.LENGTH_LONG).show()
                        }
                    })
        }

        mAuth = FirebaseAuth.getInstance()

       if(mAuth?.currentUser != null){
           val intent = Intent(this, MainActivity::class.java)
           startActivity(intent)
       }else{
           signIn()
       }

    }

    private fun signIn(){

            val providers = Arrays.asList(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.FacebookBuilder().build())

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "LOGON FAILED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val SIGN_OUT = 104
        val FROM = "from"
    }
}
