package com.marcustwichel.recipefinder.api

import android.util.Log
import com.marcustwichel.recipefinder.recipefinder.api.RecipieAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class FirebaseWorker{


    val TAG = "FirebaseAPI"

    private val service : FirebaseAPI

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://us-central1-recipefinder-200603.cloudfunctions.net/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(FirebaseAPI::class.java)
    }

    fun moveCheckedToKitchen(callback : Callback<String>, secret: String, userId: String) {
        val hash = hashString("SHA-256", secret).toLowerCase()
        Log.d(TAG, hash)
        val call = service.moveCheckedToKitchen(hash, userId)
        Log.d(TAG, call.request().url().toString())
        call.enqueue(callback)
    }

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }
}