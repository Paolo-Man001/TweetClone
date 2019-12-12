package com.paolo_manlunas.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.paolo_manlunas.twitterclone.R

class ProfileActivity : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_profile)
   }


   //-- Click Listener
   fun onApply(view: View) {

   }

   fun onLogout(view: View) {

   }

   //--- Intent
   companion object {
      fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
   }
}
