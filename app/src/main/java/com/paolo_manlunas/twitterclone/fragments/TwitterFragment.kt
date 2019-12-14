package com.paolo_manlunas.twitterclone.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paolo_manlunas.twitterclone.adapters.TweetListAdapter
import com.paolo_manlunas.twitterclone.listeners.IHomeCallback
import com.paolo_manlunas.twitterclone.listeners.ITweetListener
import com.paolo_manlunas.twitterclone.listeners.TwitterListenerImpl
import com.paolo_manlunas.twitterclone.util.User
import java.lang.RuntimeException

abstract class TwitterFragment : Fragment() {

   protected var tweetsAdapter: TweetListAdapter? = null
   protected var currentUser: User? = null
   protected val firebaseDB = FirebaseFirestore.getInstance()
   protected val userId = FirebaseAuth.getInstance().currentUser?.uid
   protected var listenerI: TwitterListenerImpl? = null
   protected var callback: IHomeCallback? = null


   // Instantiate Interface callback for use by other classes
   override fun onAttach(context: Context) {
      super.onAttach(context)
      if (context is IHomeCallback) {
      callback = context  // Attaches the Interface to the current context(Activity) which it's implemented
      } else {
         throw RuntimeException("$context must implement IHomeCallback")
      }
   }

   fun setUser(user: User?) {
      this.currentUser = user
      listenerI?.user = user
   }

   abstract fun updateList()

   override fun onResume() {
      super.onResume()
      updateList()
   }
}