package com.paolo_manlunas.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.util.DATA_USERS
import com.paolo_manlunas.twitterclone.util.DATA_USER_EMAIL
import com.paolo_manlunas.twitterclone.util.DATA_USER_USERNAME
import com.paolo_manlunas.twitterclone.util.User
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

   private val firebaseAuth = FirebaseAuth.getInstance()
   private val firebaseDB = FirebaseFirestore.getInstance()
   private val userId = FirebaseAuth.getInstance().currentUser?.uid

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_profile)


      if (userId == null) {
         finish()
      }

      profileProgressLayout.setOnTouchListener { v, event -> true }

      populateInfo()
   }

   fun populateInfo() {
      profileProgressLayout.visibility = View.VISIBLE    // Progressbar ON
      firebaseDB.collection(DATA_USERS).document(userId!!).get()
         .addOnSuccessListener {
            // document-Snapshot(it) cast as UserObject
            val user = it.toObject(User::class.java)

            // Update the properties in User for current user:
            usernameET.setText(user?.username, TextView.BufferType.EDITABLE)
            emailET.setText(user?.email, TextView.BufferType.EDITABLE)

            profileProgressLayout.visibility = View.GONE    // Progressbar OFF
         }
         .addOnFailureListener {
            it.printStackTrace()
            finish()
         }
   }


   //-- Click Listeners:

   /**   onApply() Updates the firebaseDB with the new
    *       data changes set by the user.
    * */
   fun onApply(view: View) {
      profileProgressLayout.visibility = View.VISIBLE    // Progressbar ON
      val username = usernameET.text.toString()
      val email = emailET.text.toString()
      val map = HashMap<String, Any>()
      map[DATA_USER_USERNAME] = username
      map[DATA_USER_EMAIL] = email

      firebaseDB.collection(DATA_USERS).document(userId!!).update(map)
         .addOnSuccessListener {
            Toast.makeText(this, "Update is Successful!", Toast.LENGTH_SHORT).show()
            finish()
         }
         .addOnFailureListener {
            it.printStackTrace()
            Toast.makeText(this, "Update Failed. Please try again.", Toast.LENGTH_SHORT).show()
            profileProgressLayout.visibility = View.GONE    // Progressbar OFF
         }
   }

   fun onSignOut(view: View) {
      firebaseAuth.signOut()
      finish()
   }

   //--- Intent
   companion object {
      fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
   }
}
