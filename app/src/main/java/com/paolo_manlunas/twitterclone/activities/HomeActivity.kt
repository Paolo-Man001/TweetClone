package com.paolo_manlunas.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TableLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.fragments.HomeFragment
import com.paolo_manlunas.twitterclone.fragments.MyActivityFragment
import com.paolo_manlunas.twitterclone.fragments.SearchFragment
import com.paolo_manlunas.twitterclone.fragments.TwitterFragment
import com.paolo_manlunas.twitterclone.listeners.IHomeCallback
import com.paolo_manlunas.twitterclone.util.DATA_USERS
import com.paolo_manlunas.twitterclone.util.User
import com.paolo_manlunas.twitterclone.util.loadUrl
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), IHomeCallback {

   private val firebaseAuth = FirebaseAuth.getInstance()
   private val firebaseDB = FirebaseFirestore.getInstance()
   private var user: User? = null
   // check a logged-in user:
   private var userId = FirebaseAuth.getInstance().currentUser?.uid

   private var sectionPagerAdapter: SectionPagerAdapter? = null
   private val homeFragment = HomeFragment()
   private val searchFragment = SearchFragment()
   private val myActivityFragment = MyActivityFragment()
   private var currentFragment: TwitterFragment = homeFragment


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_home)

      /** Link the container(@id of ViewPager widget from .xml)
       *  to 'this' adapter for our fragments to be inflated onto.
       * */
      sectionPagerAdapter = SectionPagerAdapter((supportFragmentManager))
      container.adapter = sectionPagerAdapter
      /** Link Tabs -> Container:
       *     - pass the @id of TabLayout we want listener to be added onto
       *  */
      container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
      /** Link Container -> Tabs:
       *     - connect container that corresponds to the selected tab
       * */
      tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

      tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
         override fun onTabReselected(p0: TabLayout.Tab?) {
         }

         override fun onTabUnselected(p0: TabLayout.Tab?) {
         }

         override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
               0 -> {
                  titleBar.visibility = View.VISIBLE
                  titleBar.text = getString(R.string.titlebar_title_home)
                  searchBar.visibility = View.GONE
                  currentFragment = homeFragment
               }
               1 -> {
                  titleBar.visibility = View.VISIBLE
                  searchBar.visibility = View.VISIBLE
                  currentFragment = searchFragment
               }
               2 -> {
                  titleBar.visibility = View.VISIBLE
                  titleBar.text = getString(R.string.titlebar_title_my_activity)
                  searchBar.visibility = View.GONE
                  currentFragment = myActivityFragment
               }
            }
         }
      })

      // logo img-click
      logo.setOnClickListener {
         startActivity(ProfileActivity.newIntent(this))
      }

      // Fab OnClick
      fab.setOnClickListener {
         startActivity(TweetActivity.newIntent(this, userId, user?.username))
      }

      homeProgressLayout.setOnTouchListener { v, event -> true }


      // Search-bar Function/listener
      search.setOnEditorActionListener { v, actionId, event ->
         if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchFragment.newHashtag(v?.text.toString())
         }
         return@setOnEditorActionListener true
      }

   }


   // Check for a logged-in user ?: go to LoginActivity
   override fun onResume() {
      super.onResume()
      // Updates about who the current user is
      userId = FirebaseAuth.getInstance().currentUser?.uid
      if (userId == null) {
         startActivity(LoginActivity.newIntent(this))
         finish()
      } else {

         // Update Home logoImg
         populate()
      }
   }


   // Member from IHomeCallback Interface
   override fun onUserUpdated() {
      populate()
   }

   private fun populate() {
      homeProgressLayout.visibility = View.VISIBLE

      firebaseDB.collection(DATA_USERS).document(userId!!).get()
         .addOnSuccessListener { documentSnapshot ->

            homeProgressLayout.visibility = View.GONE
            user = documentSnapshot.toObject(User::class.java)
            user?.imageUrl?.let { imgUrl ->
               logo.loadUrl(imgUrl, R.drawable.logo)
            }

            updateFragmentUser()
         }
         .addOnFailureListener {
            it.printStackTrace()
            finish()
         }
   }

   private fun updateFragmentUser() {
      homeFragment.setUser(user)
      searchFragment.setUser(user)
      myActivityFragment.setUser(user)
      currentFragment.updateList()
   }


   // Pager Adapter(Fragment Adapters for the tab pages)
   inner class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
      override fun getItem(position: Int): Fragment {
         return when (position) {
            0 -> homeFragment
            1 -> searchFragment
            else -> myActivityFragment
         }
      }

      override fun getCount() = 3
   }


   //--- Intent
   companion object {
      fun newIntent(context: Context) = Intent(context, HomeActivity::class.java)
   }

}
