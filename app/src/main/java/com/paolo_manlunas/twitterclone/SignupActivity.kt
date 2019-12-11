package com.paolo_manlunas.twitterclone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paolo_manlunas.twitterclone.util.DATA_USERS
import com.paolo_manlunas.twitterclone.util.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

   private val firebaseDB = FirebaseFirestore.getInstance()

   private val firebaseAuth = FirebaseAuth.getInstance()

   private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
      val user = firebaseAuth.currentUser?.uid
      user?.let {
         startActivity(HomeActivity.newIntent(this))
      }
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_signup)

      setTextChangeListener(usernameET, usernameTIL)
      setTextChangeListener(emailET, emailTIL)
      setTextChangeListener(passwordET, passwordTIL)

      signupProgressLayout.setOnTouchListener { v, event -> true }
   }


   // Text-Watcher
   fun setTextChangeListener(et: TextInputEditText, til: TextInputLayout) {
      et.addTextChangedListener(object : TextWatcher {
         override fun afterTextChanged(s: Editable?) {
         }

         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
         }

         /* when user starts typing, error message disappears. */
         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            til.isErrorEnabled = false
         }
      })
   }


   // Signup
   fun onSignup(view: View) {
      var proceed = true

      // Username Check
      if (usernameET.text.isNullOrEmpty()) {
         usernameTIL.error = "Username is required"
         usernameTIL.isErrorEnabled = true
         proceed = false
      }

      // Email Check
      if (emailET.text.isNullOrEmpty()) {
         emailTIL.error = "Email is required"
         emailTIL.isErrorEnabled = true
         proceed = false
      }

      // Password Check
      if (passwordET.text.isNullOrEmpty()) {
         passwordTIL.error = "Password is required"
         passwordTIL.isErrorEnabled = true
         proceed = false
      }

      // if Signup proceeds
      if (proceed) {
         // show progressbar
         signupProgressLayout.visibility = View.VISIBLE

         // Signup with Firebase Auth
         firebaseAuth.createUserWithEmailAndPassword(
            emailET.text.toString(),
            passwordET.text.toString()
         )
            .addOnCompleteListener {
               if (!it.isSuccessful) {
                  Toast.makeText(
                     this@SignupActivity,
                     "Signup Error: ${it.exception?.localizedMessage}",
                     Toast.LENGTH_LONG
                  ).show()
               } else {
                  /* Create User in FirebaseDB */
                  val email = emailET.text.toString()
                  val username = usernameET.text.toString()

                  // User data class Object (from util)
                  val user = User(email, username, "", arrayListOf(), arrayListOf())

                  /* Add 'user' Object to DB:
                  *  firebaseAuth.uid!! connects the the Auth user and corresponding DB with UID.
                  * */
                  firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)
               }
               signupProgressLayout.visibility = View.GONE
            }
            .addOnFailureListener {
               it.printStackTrace()
               signupProgressLayout.visibility = View.GONE
            }
      }
   }

   // Go-to-Login
   fun goToLogin(view: View) {
      startActivity(LoginActivity.newIntent(this))
      finish()
   }


   /* when SignupActivity starts up, we have to attach this to FirebaseAuth
   *  to check if user is already signUp.
   * */
   override fun onStart() {
      super.onStart()

      // pass the firebaseAuthListener to see if user is an existing user
      firebaseAuth.addAuthStateListener(firebaseAuthListener)
   }

   /* when screen starts up, we have to detach/remove  this from FirebaseAuth */
   override fun onStop() {
      super.onStop()
      firebaseAuth.removeAuthStateListener(firebaseAuthListener)
   }

   //--- Intent
   companion object {
      fun newIntent(context: Context) = Intent(context, SignupActivity::class.java)
   }

}
