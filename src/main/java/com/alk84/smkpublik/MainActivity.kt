package com.alk84.smkpublik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import onboarding.OnboardingOneActivity

/*
ini adalah activity pertama yang akan dirun
tidak ada fitur spesial disini hanya
melakukan pending beberapa detikb saja.
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@MainActivity,
                OnboardingOneActivity::class.java)
            startActivity(intent)
            finish()
        },3000)

    }
}

