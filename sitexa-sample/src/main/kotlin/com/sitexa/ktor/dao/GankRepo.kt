package com.sitexa.ktor.dao

/**
 * Created by open on 11/06/2017.
 */


data class History(val date:String,val content:String)

data class PublishedDate(val error:Boolean,val results:List<String>)

data class Result(val error:Boolean,val results:List<Article>)

data class Article(val _id:String,val createdAt:String,val desc:String,
                   var images:Array<String>,val publishedAt:String,val source:String,val type:String,val url:String,
                   val used:Boolean,val who:String)
