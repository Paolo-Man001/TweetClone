package com.paolo_manlunas.twitterclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    // On-Login
    fun onLogin(view: View) {
        Toast.makeText(this,"Login CLICKED",Toast.LENGTH_SHORT).show()
    }

    // Go-to-Signup
    fun goToSignup(view: View) {
        Toast.makeText(this,"Signup CLICKED",Toast.LENGTH_SHORT).show()
    }
}
