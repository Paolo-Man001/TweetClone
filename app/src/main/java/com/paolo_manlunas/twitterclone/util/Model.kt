package com.paolo_manlunas.twitterclone.util

data class User(
   val email: String? = "",
   val username: String? = "",
   val imageUrl: String? = "",
   val followHash: ArrayList<String>? = arrayListOf(),   // contains hashTags the user follows
   val followUsers: ArrayList<String>? = arrayListOf()   // contains other users this user follows
)

data class Tweet(
   val tweetId: String? = "",
   val userIds: ArrayList<String>? = arrayListOf(),   // contains the users that Tweeted this Tweet
   val username: String? = "",
   val text: String? = "",
   val imageUrl: String?="",
   val timestamp: Long? = 0,
   val hashtags: ArrayList<String>? = arrayListOf(),  // contains the hashTags this Tweet have
   val likes: ArrayList<String>? = arrayListOf()      // contains userIds who Liked this Tweet
)