package com.example.birder.wikiJson

data class Page(
    val ns: Int,
    val original: Original,
    val pageid: Int,
    val terms: Terms,
    val title: String
)