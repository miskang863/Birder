package com.example.birder.imageApi

data class Page(
    val ns: Int,
    val original: Original,
    val pageid: Int,
    val terms: Terms,
    val title: String
)