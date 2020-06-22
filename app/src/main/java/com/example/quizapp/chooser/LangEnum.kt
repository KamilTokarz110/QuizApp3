package com.example.quizapp.chooser

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.quizapp.QApp
import com.example.quizapp.R

//Mapowanie lezykow i zasobow
enum class LangEnum(@StringRes val label: Int,
    @DrawableRes val image : Int) {
    ANDROID(R.string.lang_android, R.drawable.ic_language_android),
    KOTLIN(R.string.lang_android, R.drawable.ic_language_kotlin),
    JAVA(R.string.lang_android, R.drawable.ic_language_java);

    fun getString()=
        QApp.res.getString(this.label)
}