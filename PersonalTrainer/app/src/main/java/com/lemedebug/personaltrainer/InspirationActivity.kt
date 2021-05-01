package com.lemedebug.personaltrainer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.gson.GsonBuilder
import com.lemedebug.personaltrainer.models.RandomQuote
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_bmi.*
import kotlinx.android.synthetic.main.activity_inspiration.*
import java.io.IOException

class InspirationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspiration)


        setSupportActionBar(toolbar_inspiration_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //set back button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title="INSPIRATIONAL QUOTE"

        toolbar_inspiration_activity.setNavigationOnClickListener {
            onBackPressed()
        }



        val client = OkHttpClient()

        val request = Request.Builder()
                .url("https://bodybuilding-quotes.p.rapidapi.com/random-quote")
                .get()
                .addHeader("x-api-key", "{{api-key}}")
                .addHeader("x-rapidapi-key", "54ccb894fcmsh667d508b2a8d497p1ecad7jsn2b35348e28d4")
                .addHeader("x-rapidapi-host", "bodybuilding-quotes.p.rapidapi.com")
                .build()

        client.newCall(request).enqueue(object : Callback {
            var mainHandler = Handler(this@InspirationActivity.mainLooper)

            override fun onFailure(request: Request?, e: IOException?) {
                Log.i("RESPONSE_RAPID_API","API execution failed")
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(response: Response?) {
                mainHandler.post {
                    val body = response?.body()?.string() ?: return@post
                    val gsonBody = GsonBuilder().create()
                    val randomQuote = gsonBody.fromJson(body, RandomQuote::class.java)
                    tv_quote.text = randomQuote.quote
                    tv_author_name.text = "- ${randomQuote.author}"
                }
            }
        })


        // Animate the image in the quote
        tv_quote.animate().apply {
            duration = 1000 // 1 second
            rotationY(360f) // rotate 360 degrees on Y axis
        }.start()


    }


}