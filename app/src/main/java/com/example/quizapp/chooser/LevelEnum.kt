package com.example.quizapp.chooser

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.quizapp.QApp
import com.example.quizapp.R

enum class LevelEnum (
    @StringRes val label : Int,
    @DrawableRes
    val image : Int)
{
        EASY(R.string.level_easy, R.drawable.ic_level_easy),
        AVERAGE(R.string.level_average, R.drawable.ic_level_average),
        HARD(R.string.level_hard, R.drawable.ic_level_hard);

        fun getString()=
            QApp.res.getString(this.label)

    }




