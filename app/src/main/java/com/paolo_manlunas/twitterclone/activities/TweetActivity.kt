package com.paolo_manlunas.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.paolo_manlunas.twitterclone.R

class TweetActivity : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_tweet)
   }


   // AddImage
   fun addImage(view: View) {
      Toast.makeText(this,"AddImage CLICKED!",Toast.LENGTH_SHORT).show()
   }

   // Send Tweet
   fun postTweet(view: View) {
      Toast.makeText(this,"PostTweet CLICKED!",Toast.LENGTH_SHORT).show()
   }

   //--- Intent
   companion object {
      val PARAM_USER_ID = "UserId"
      val PARAM_USER_NAME = "UserName"

      fun newIntent(context: Context, userId: String?, userName: String?): Intent {
         val intent = Intent(context, TweetActivity::class.java)
         intent.putExtra(PARAM_USER_ID,userId)
         intent.putExtra(PARAM_USER_NAME,userName)

         return intent
      }
   }
}
