package com.paolo_manlunas.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.util.DATA_TWEETS
import com.paolo_manlunas.twitterclone.util.Tweet
import kotlinx.android.synthetic.main.activity_tweet.*

class TweetActivity : AppCompatActivity() {

   private val firebaseDB = FirebaseFirestore.getInstance()
   private val firebseStorage = FirebaseStorage.getInstance()
   private val imageUrl: String? = null
   private var userId: String? = null
   private var userName: String? = null


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_tweet)


      if (intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_NAME)) {
         userId = intent.getStringExtra((PARAM_USER_ID))
         userName = intent.getStringExtra((PARAM_USER_NAME))
      } else {
         Toast.makeText(this, "Error creating tweet", Toast.LENGTH_SHORT).show()
         finish()
      }

      tweetProgressLayout.setOnTouchListener { v, event -> true }
   }


   // AddImage
   fun addImage(view: View) {
   }

   // Send Tweet
   fun postTweet(view: View) {
      tweetProgressLayout.visibility = View.VISIBLE

      // GET: data
      val text = tweetText.text.toString()
      val hashtags = getHashtags(text)

      // POST: to update DB
      val tweetId = firebaseDB.collection(DATA_TWEETS).document() // Create Tweets Collection
      val tweet = Tweet(      // Tweet model
         tweetId.id,
         arrayListOf(userId!!),
         userName,
         text,
         imageUrl,
         System.currentTimeMillis(),
         hashtags,
         arrayListOf()
      )

      tweetId.set(tweet)
         .addOnCompleteListener { finish() }
         .addOnFailureListener {
            it.printStackTrace()
            tweetProgressLayout.visibility = View.GONE
            Toast.makeText(this, "Failed to post tweet", Toast.LENGTH_SHORT).show()
         }
   }

   private fun getHashtags(source: String): ArrayList<String> {
      val hashtags = arrayListOf<String>()
      var text = source   // assign source into variable

      while (text.contains("#")) {
         var hashtag = ""
         val hash = text.indexOf("#")
         text = text.substring(hash + 1)

         val firstSpace = text.indexOf(" ")
         val firstHash = text.indexOf("#")

         // Filter source of '#' and 'space'  -- Can use RegEx
         if (firstSpace == -1 && firstHash == -1) {
            hashtag = text.substring(0)
         } else if (firstSpace != -1 && firstSpace < firstHash) {
            hashtag = text.substring(0, firstSpace)
            text = text.substring(firstSpace + 1)
         } else {
            hashtag = text.substring(0, firstHash)
            text = text.substring(firstHash)
         }

         // Add filtered source into List
         if (!hashtag.isNullOrEmpty()) {
            hashtags.add(hashtag)
         }
      }

      return hashtags
   }

   //--- Intent
   companion object {
      val PARAM_USER_ID = "UserId"
      val PARAM_USER_NAME = "UserName"

      fun newIntent(context: Context, userId: String?, userName: String?): Intent {
         val intent = Intent(context, TweetActivity::class.java)
         intent.putExtra(PARAM_USER_ID, userId)
         intent.putExtra(PARAM_USER_NAME, userName)

         return intent
      }
   }
}
