package com.paolo_manlunas.twitterclone.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.util.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

   private val firebaseAuth = FirebaseAuth.getInstance()
   private val firebaseDB = FirebaseFirestore.getInstance()
   private val userId = FirebaseAuth.getInstance().currentUser?.uid

   private val firebaseStorage = FirebaseStorage.getInstance().reference
   private var imageUrl: String? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_profile)


      if (userId == null) {
         finish()
      }

      profileProgressLayout.setOnTouchListener { v, event -> true }


      /** The Intent refers to the intent action for picking the Photo from the device's storage */
      photoIV.setOnClickListener {
         val intent = Intent(Intent.ACTION_PICK)
         intent.type = "image/*"
         // when starting for activityResult, override onActivityResult()
         startActivityForResult(intent, REQUEST_CODE_PHOTO)
      }

      populateInfo()
   }


   fun populateInfo() {
      profileProgressLayout.visibility = View.VISIBLE    // Progressbar ON
      firebaseDB.collection(DATA_USERS).document(userId!!).get()
         .addOnSuccessListener { documentSnapshot ->

            val user = documentSnapshot.toObject(User::class.java)   // cast as User Obj.

            // Update the properties in User for current user:
            usernameET.setText(user?.username, TextView.BufferType.EDITABLE)  // username
            emailET.setText(user?.email, TextView.BufferType.EDITABLE)        // email
            imageUrl = user?.imageUrl                                         // image

            imageUrl?.let { imgUrl ->
               // show image in DB
               photoIV.loadUrl(imgUrl, R.drawable.logo)
            }

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


   /** Process onActivityResult */
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
         storeImage(data?.data)
      }
   }

   private fun storeImage(imageUri: Uri?) {
      //update DB with the correct imageUrl
      imageUri?.let {
         Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()
         profileProgressLayout.visibility = View.VISIBLE

         val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)
         filePath.putFile(imageUri)
            .addOnSuccessListener {

               filePath.downloadUrl
                  .addOnSuccessListener { uri ->

                     val url = uri.toString()    // get Uri
                     // save the Uri in the DB collection>>document>>"imageUrl"
                     firebaseDB.collection(DATA_USERS).document(userId!!)
                        .update(DATA_USER_IMAGE_URL, url)
                        .addOnSuccessListener {

                           imageUrl = url
                           photoIV.loadUrl(imageUrl, R.drawable.logo)
                        }

                     profileProgressLayout.visibility = View.GONE
                  }
                  .addOnFailureListener {
                     onUploadFailure()
                  }
            }
            .addOnFailureListener {
               onUploadFailure()
            }

      }
   }

   private fun onUploadFailure() {
      Toast.makeText(this, "Image upload Failed! Try again..", Toast.LENGTH_SHORT)
         .show()

      profileProgressLayout.visibility = View.GONE
   }


   //-------------------------------
   fun onSignOut(view: View) {
      firebaseAuth.signOut()
      finish()
   }

   //--- Intent
   companion object {
      fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
   }
}
