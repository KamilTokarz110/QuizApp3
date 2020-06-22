package com.example.quizapp

import com.example.quizapp.profile.UserItem
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUserItem(): UserItem{
    return UserItem().apply {
        uid = this@toUserItem.uid
        url = this@toUserItem.toString()
        name = this@toUserItem.displayName!!
    }
}