package com.example.quizapp.chooser

import java.io.Serializable

//Model danych dla pojedyńczego quizu

data class QuizItem(
    var level:LevelEnum = LevelEnum.EASY,
    var lang:LangEnum = LangEnum.ANDROID,
    var questset: String = "") : Serializable
