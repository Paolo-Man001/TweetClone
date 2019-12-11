package com.paolo_manlunas.twitterclone.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.paolo_manlunas.twitterclone.R
import com.paolo_manlunas.twitterclone.fragments.HomeFragment
import com.paolo_manlunas.twitterclone.fragments.MyActivityFragment
import com.paolo_manlunas.twitterclone.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

   private val firebaseAuth = FirebaseAuth.getInstance()


   private var sectionPagerAdapter: SectionPagerAdapter? = null
   private val homeFragment = HomeFragment()
   private val searchFragment = SearchFragment()
   private val myActivityFragment = MyActivityFragment()

   // check a logged-in user:
   private val userId = FirebaseAuth.getInstance().currentUser?.uid


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

         override fun onTabSelected(p0: TabLayout.Tab?) {
         }

      })
   }


   // Check for a logged-in user ?: go to LoginActivity
   override fun onResume() {
      super.onResume()
      if (userId == null) {
         startActivity(LoginActivity.newIntent(this))
         finish()
      }
   }


   // Logout
   fun onLogout(view: View) {
      firebaseAuth.signOut()
      startActivity(LoginActivity.newIntent(this))
      finish()
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
