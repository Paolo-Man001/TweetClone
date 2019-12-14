package com.paolo_manlunas.twitterclone.listeners

import androidx.appcompat.app.AlertDialog
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
      tweet?.let {
         val owner = tweet.userIds?.get(0)  // userIds[0] is Always the owner's
         if (owner != userId) {
            if (user?.followUsers?.contains(owner) == true) {
               AlertDialog.Builder(tweetList.context)
                  .setTitle("Unfollow ${tweet.username}?")
                  .setPositiveButton("Yes") { dialog, which ->
                     tweetList.isClickable = false

                     var followedUsers = user?.followUsers
                     if (followedUsers == null) {
                        followedUsers= arrayListOf()
                     }

                     followedUsers?.remove(owner)

                     firebaseDB.collection(DATA_USERS).document(userId!!)
                        .update(DATA_USER_FOLLOW, followedUsers)
                        .addOnSuccessListener {
                           tweetList.isClickable = true
                           callback?.onUserUpdated()
                        }
                        .addOnFailureListener {
                           tweetList.isClickable = true
                        }
                  }
                  .setNegativeButton("Cancel") { dialog, which -> }
                  .show()
            } else {
               AlertDialog.Builder(tweetList.context)
                  .setTitle("Follow ${tweet.username}?")
                  .setPositiveButton("Yes") { dialog, which ->
                     tweetList.isClickable = false

                     var followedUsers = user?.followUsers
                     if (followedUsers == null) {
                        followedUsers= arrayListOf()
                     }

                     owner?.let{
                        followedUsers?.add(owner)

                        firebaseDB.collection(DATA_USERS).document(userId!!)
                           .update(DATA_USER_FOLLOW, followedUsers)
                           .addOnSuccessListener {
                              tweetList.isClickable = true
                              callback?.onUserUpdated()
                           }
                           .addOnFailureListener {
                              tweetList.isClickable = true
                           }
                     }
                  }
                  .setNegativeButton("Cancel") { dialog, which -> }
                  .show()
            }
         }
      }
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