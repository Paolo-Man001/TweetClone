package com.paolo_manlunas.twitterclone.fragments


import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.adapters.TweetListAdapter
import com.paolo_manlunas.twitterclone.listeners.TweetListener
import com.paolo_manlunas.twitterclone.util.DATA_TWEETS
import com.paolo_manlunas.twitterclone.util.DATA_TWEET_HASHTAGS
import com.paolo_manlunas.twitterclone.util.Tweet
import com.paolo_manlunas.twitterclone.util.User
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * Search Fragment subclass.
 */
class SearchFragment : TwitterFragment() {

   private var currentHashtag = ""
   private var tweetsAdapter: TweetListAdapter? = null
   private var currentUser: User? = null
   private val firebaseDB = FirebaseFirestore.getInstance()
   private val userId = FirebaseAuth.getInstance().currentUser?.uid
   private val listener: TweetListener? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View? {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_search, container, false)
   }


   /** Instantiate the List when this Fragment is Created: */
   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)

      tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
      tweetsAdapter?.setListener(listener)

      // instantiate tweetList from xml
      tweetList.apply {
         layoutManager = LinearLayoutManager(context)
         adapter = tweetsAdapter
         // makes the elements VERTICAL
         addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      }

      // Add Function for SwipeRefreshLayout
      swipeRefresh.setOnRefreshListener {
         swipeRefresh.isRefreshing=false
         updateList()
      }
   }

   fun newHashtag(term: String) {
      currentHashtag = term
      followHashtag.visibility = View.VISIBLE

      updateList()
   }

   private fun updateList() {
      tweetList?.visibility = View.GONE
      firebaseDB.collection(DATA_TWEETS)
         .whereArrayContains(DATA_TWEET_HASHTAGS, currentHashtag).get()
         .addOnSuccessListener { list ->
            tweetList?.visibility = View.VISIBLE
            val tweets = arrayListOf<Tweet>()
            for (document in list.documents) {
               val tweet = document.toObject(Tweet::class.java)   // convert into Tweet Obj
               tweet?.let { tweets.add(it) }
            }

            // sort tweet-list by date (timestamp)
            val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
            tweetsAdapter?.updateTweets(sortedTweets)
         }
         .addOnFailureListener {
            it.printStackTrace()
         }
   }
}
