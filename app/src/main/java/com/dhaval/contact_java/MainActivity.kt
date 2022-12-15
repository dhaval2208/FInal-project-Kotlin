package com.dhaval.contact_java

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "CONTACT"
    }

    fun addcontact(view: View?) {
        startActivity(Intent(this, MainActivity2::class.java))
    }
}
