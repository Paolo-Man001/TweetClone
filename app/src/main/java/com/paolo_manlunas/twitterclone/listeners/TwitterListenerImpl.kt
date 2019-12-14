package com.paolo_manlunas.twitterclone.listeners

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paolo_manlunas.twitterclone.util.DATA_TWEETS
import com.paolo_manlunas.twitterclone.util.DATA_TWEET_LIKES
import com.paolo_manlunas.twitterclone.util.Tweet
import com.paolo_manlunas.twitterclone.util.User

class TwitterListenerImpl(
   val tweetList: RecyclerView,
   var user: User?,
   val callback: IHomeCallback?
) : ITweetListener {

   private val firebaseDB = FirebaseFirestore.getInstance()
   private val userId = FirebaseAuth.getInstance().currentUser?.uid


   override fun onLayoutClick(tweet: Tweet?) {
   }

   override fun onLike(tweet: Tweet?) {
      tweet?.let {
         tweetList.isClickable = false
         val likes = tweet.likes
         if (tweet.likes?.contains(userId)!!) {
            likes?.remove(userId)
         } else {
            likes?.add(userId!!)
         }

         // UPDATE Db
         firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
            .update(DATA_TWEET_LIKES, likes)
            .addOnSuccessListener {
               tweetList.isClickable = true
               callback?.onRefresh()
            }
            .addOnFailureListener {
               tweetList.isClickable = true
            }
      }
   }

   override fun onRetweet(tweet: Tweet?) {
   }
}