package com.paolo_manlunas.twitterclone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

   private val firebaseAuth = FirebaseAuth.getInstance()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_home)
   }


   // Logout
   fun onLogout(view: View) {
      firebaseAuth.signOut()
      startActivity(LoginActivity.newIntent(this))
      finish()
   }


   //--- Intent
   companion object {
      fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
   }
}
