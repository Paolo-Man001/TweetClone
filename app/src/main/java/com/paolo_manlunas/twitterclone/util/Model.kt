package com.paolo_manlunas.twitterclone.util

data class User(
   val email: String? = "",
   val username: String? = "",
   val imageUrl: String? = "",
   val followHash: ArrayList<String>? = arrayListOf(),
   val followUsers: ArrayList<String>? = arrayListOf()
)