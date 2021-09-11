package com.example.data.models

import com.example.utility.Constants.TYPE_CHOSEN_WORD

data class ChosenWord(
    val chosenWord: String,
    val roomName: String
): BaseModel(TYPE_CHOSEN_WORD)
