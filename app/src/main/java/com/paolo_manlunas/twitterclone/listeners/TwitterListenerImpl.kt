package com.paolo_manlunas.twitterclone.listeners

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paolo_manlunas.twitterclone.util.*

class TwitterListenerImpl(
   val tweetList: RecyclerView,
   var user: User?,
   val callback: IHomeCallback?
) : ITweetListener {

   private val firebaseDB = FirebaseFirestore.getInstance()
   private val userId = FirebaseAuth.getInstance().currentUser?.uid


   override fun onLayoutClick(tweet: Tweet?) {
   }

   // When Like-Icon is CLICKED!
   override fun onLike(tweet: Tweet?) {
      tweet?.let {
         tweetList.isClickable = false

         val likes = tweet.likes // contains userIds who liked this Tweet
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

   // When ReTweet-Icon is CLICKED!
   override fun onRetweet(tweet: Tweet?) {
      tweet?.let {
         tweetList.isClickable = false

         val retweets = tweet.userIds // contains userIds who tweeted this Tweet
         if (retweets?.contains(userId)!!) {
            retweets.remove(userId)
         } else {
            retweets.add(userId!!)
         }

         // UPDATE Db
         firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!)
            .update(DATA_TWEET_USER_IDS, retweets)
            .addOnSuccessListener {
               tweetList.isClickable = true
               callback?.onRefresh()
            }
            .addOnFailureListener {
               tweetList.isClickable = true
            }
      }
   }
}