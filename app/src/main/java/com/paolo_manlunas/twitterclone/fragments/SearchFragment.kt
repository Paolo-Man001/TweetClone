package com.paolo_manlunas.twitterclone.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.adapters.TweetListAdapter
import com.paolo_manlunas.twitterclone.listeners.TwitterListenerImpl
import com.paolo_manlunas.twitterclone.util.*
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * Search Fragment subclass.
 */
class SearchFragment : TwitterFragment() {

   private var currentHashtag = ""
   private var hashtagFollowed = false

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

      listenerI = TwitterListenerImpl(tweetList, currentUser, callback)

      tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
      tweetsAdapter?.setListener(listenerI)

      // instantiate tweetList from xml
      tweetList.apply {
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

      // FOLLOW Hashtag:
      followHashtag.setOnClickListener {
         followHashtag.isClickable = false
         // Get list of hashtags followed by currentUser
         val followed = currentUser?.followHash
         if (hashtagFollowed) {
            followed?.remove(currentHashtag) // REMOVE from list
         } else {
            followed?.add(currentHashtag)    // ADD the currentHashtag into 'followed'
         }
         // update DB with the new 'followed' hashtag list
         firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USER_HASTAGS, followed)
            .addOnSuccessListener {
               callback?.onUserUpdated()        // call onUserUpdated() in HomeActivity
               followHashtag.isClickable = true
            }
            .addOnFailureListener {
               it.printStackTrace()
               followHashtag.isClickable = true
            }
      }
   }


   fun newHashtag(term: String) {
      currentHashtag = term
      followHashtag.visibility = View.VISIBLE
      updateList()
   }

   private fun updateFollowDrawable() {
      hashtagFollowed = currentUser?.followHash?.contains(currentHashtag) == true
      context?.let {
         if (hashtagFollowed) {
            followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow))
         } else {
            followHashtag.setImageDrawable(
               ContextCompat.getDrawable(
                  it,
                  R.drawable.follow_inactive
               )
            )
         }
      }
   }

   override fun updateList() {
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

      updateFollowDrawable()     // Update 'follow(star)' icon
   }
}
