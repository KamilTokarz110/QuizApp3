package com.example.quizapp.quiz

import java.io.Serializable
//Model danych dla pojedyńczego pytania
data class QuestionItem(
    var ask: String ="Pytanie",
    var positive : String = "pos",
    var false1: String = "false1",
    var false2: String = "false2" ): Serializable
