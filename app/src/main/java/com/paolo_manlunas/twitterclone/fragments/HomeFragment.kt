package com.paolo_manlunas.twitterclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.adapters.TweetListAdapter
import com.paolo_manlunas.twitterclone.listeners.TwitterListenerImpl
import com.paolo_manlunas.twitterclone.util.DATA_TWEETS
import com.paolo_manlunas.twitterclone.util.DATA_TWEET_HASHTAGS
import com.paolo_manlunas.twitterclone.util.DATA_TWEET_USER_IDS
import com.paolo_manlunas.twitterclone.util.Tweet
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.ArrayList

/**
 * Home Fragment subclass.
 */
class HomeFragment : TwitterFragment() {


   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View? {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_home, container, false)
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      listenerI = TwitterListenerImpl(tweetList, currentUser, callback)

      tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
      tweetsAdapter?.setListener(listenerI)
      tweetList?.apply {
         layoutManager = LinearLayoutManager(context)
         adapter = tweetsAdapter
         // makes the elements VERTICAL
         addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      }

      // ADD FUNCTION for SwipeRefreshLayout
      swipeRefresh.setOnRefreshListener {
         swipeRefresh.isRefreshing = false
         updateList()
      }

   }

   /** FROM: TwitterFragment abstract method */
   override fun updateList() {
      tweetList.visibility = View.GONE
      currentUser?.let { user ->
         val tweets = arrayListOf<Tweet>()

         // Retrieve HASHTAGS this User Follows :
         for (hashtag in user.followHash!!) {
            firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, hashtag)
               .get()
               .addOnSuccessListener { list ->
                  for (document in list.documents) {
                     val tweet = document.toObject(Tweet::class.java)

                     tweet?.let { tweets.add(it) } // add to tweets arrayList
                  }
                  updateAdapter(tweets)
                  tweetList?.visibility = View.VISIBLE
               }
               .addOnFailureListener {
                  it.printStackTrace()
                  tweetList?.visibility = View.VISIBLE
               }
         }


         // Retrieve other USERS this User Follows :
         for (followedUser in user.followUsers!!) {
            firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_USER_IDS, followedUser)
               .get()
               .addOnSuccessListener { list ->
                  for (document in list.documents) {
                     val tweet = document.toObject(Tweet::class.java)

                     tweet?.let { tweets.add(it) } // add to tweets arrayList
                  }
                  updateAdapter(tweets)
                  tweetList?.visibility = View.VISIBLE
               }
               .addOnFailureListener {
                  it.printStackTrace()
                  tweetList?.visibility = View.VISIBLE
               }
         }
      }
   }

   private fun updateAdapter(tweets: ArrayList<Tweet>) {
      val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
      tweetsAdapter?.updateTweets(removeDuplicates(sortedTweets))
   }

   private fun removeDuplicates(originalList: List<Tweet>) = originalList.distinctBy { it.tweetId }

}
