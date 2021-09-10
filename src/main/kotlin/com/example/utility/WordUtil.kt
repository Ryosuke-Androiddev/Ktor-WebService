package com.example.utility

import java.io.File

val words = readWordList("resources/programmers_wordlist.txt")

//So, maybe I do not have to implement to my app.
// get file contents and create List Of it
fun readWordList(fileName: String): List<String> {
    val inputStream = File(fileName).inputStream()
    val words = mutableListOf<String>()
    inputStream.bufferedReader().forEachLine { words.add(it) }
    return words
}

// If List does not have word, this method set its words to List!
fun getRandomWords(amount: Int): List<String>{
    var curAmount = 0
    val result = mutableListOf<String>()
    while (curAmount < amount){
        val word = words.random()
        if (!result.contains(word)){
            result.add(word)
            curAmount++
        }
    }
    return result
}

// Kotlin Language
// ______ ________ if (it != ' ') '_' else ' '
// _ _ _ _ _ _  3bank here  _ _ _ _ _ _ _ joinToString(" ")

fun String.transformToUnderscores() =
    toCharArray().map{
        if (it != ' ') '_' else ' '
    }.joinToString(" ")