package com.example.quizapp

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.beardedhen.androidbootstrap.TypefaceProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

// Klasa aplikacji wystawia singletony i inicjalizuje czcionki emotowoe
class QApp : Application() {


        override fun onCreate(){
            super.onCreate()
            //czcionki
            TypefaceProvider.registerDefaultIconSets()
            //Zasoby
            res = resources

            ctx = applicationContext
            fData = FirebaseDatabase.getInstance()
            fAuth = FirebaseAuth.getInstance()
            //Użytkownik pozostanie zalogowany
            fUser = fAuth.currentUser

        }
    companion object {
        lateinit var ctx: Context
        lateinit var res: Resources
        lateinit var fData: FirebaseDatabase
        lateinit var  fAuth: FirebaseAuth

        var fUser : FirebaseUser? = null

    }

}