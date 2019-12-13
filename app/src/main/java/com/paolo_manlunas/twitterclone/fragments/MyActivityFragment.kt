package com.paolo_manlunas.twitterclone.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.paolo_manlunas.twitterclone.R

/**
 * MyActivity Fragment subclass.
 */
class MyActivityFragment : TwitterFragment() {


   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View? {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_my_activity, container, false)
   }


   /** FROM: TwitterFragment abstract method */
   override fun updateList() {
   }
}
