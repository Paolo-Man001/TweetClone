package com.paolo_manlunas.twitterclone.listeners

import com.paolo_manlunas.twitterclone.util.Tweet

interface TweetListener {
   fun onLayoutClick(tweet: Tweet?)
   fun onLike(tweet: Tweet?)
   fun onRetweet(tweet: Tweet?)
}