package com.example.quizapp.profile

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.quizapp.MainActivity
import com.example.quizapp.QApp
import com.example.quizapp.R
import com.example.quizapp.news.NewsItem
import com.example.quizapp.news.NewsListFragment
import com.example.quizapp.news.NewsListRecyclerViewAdapter
import com.example.quizapp.toUserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * Fragment profulu - Dla niezalogowanego proponuję logowanie dla zalogowanego prezentuję swój profil
 */
class ProfileFragment: Fragment() {

    var currentUser: UserItem? = null

    var feedRef: Query? = null
    var respectsValue =1

    val authListener:FirebaseAuth.AuthStateListener by lazy {
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if(firebaseAuth.currentUser != null){
                updateCurrentUser()
                updateFeedRefEventListener()
                updateLogon()
            }
            else{
                Log.d("NewsListFragment","authListener - user:null")
            }
        }
    }

    val eventListener: ValueEventListener by lazy {
        object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var pointsValue = 0
                respectsValue = 0
                for(it in p0.children){
                    val feed = it.getValue(NewsItem::class.java)!!
                    mNewsMap.put(it.key!!,feed)
                    pointsValue += feed.points
                    respectsValue+= 1 + it.child("respects").children.sumBy{ it.getValue(Int::class.java)!! }
                }
                feed_recycler?.adapter?.notifyDataSetChanged()
                respects?.text = respectsValue.toString()
                points?.text = pointsValue.toString()
                loader_profil?.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateFeedRefEventListener() {
        currentUser?.let {
            feedRef = FirebaseDatabase.getInstance().getReference("feeds").orderByChild("uid").equalTo(it.uid)
            loader_profil.visibility= View.VISIBLE
            feedRef?.addValueEventListener(eventListener)
        }
        loader_profil?.visibility = View.INVISIBLE

    }

    private val mNewsMap: HashMap<String, NewsItem> = hashMapOf()
//        //TODO usunac przy internecie
//        Pair(
//            "asd", NewsItem(
//                comment = "Super",
//                points = 10,
//                quiz = "Kotlin łatwy",
//                image = "https://cdn.stocksnap.io/img-thumbs/960w/guy-man_6R7KCXBEEE.jpg",
//                user = "Kamil Tokarz",
//                timeMilis = System.currentTimeMillis() - 1000,
//                uid = "asdwqq",
//                respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa", 1))
//            )
//        ),
//
//       Pair(
//            "jmhgjghk", NewsItem(
//                comment = "Walcze z ostrym cieniem mgły",
//                points = 10,
//                quiz = "Kotlin trudny",
//                image = "https://gfx.wiadomosci.radiozet.pl/var/radiozetwiadomosci/storage/images/polska/polityka/andrzej-duda-rapuje-w-hot16challenge2.-premiera-morawieckiego/7448954-1-pol-PL/Hot16Challenge2-w-wykonaniu-Andrzeja-Dudy.-Nominowal-premiera-Morawieckiego_article.png",
//                user = "Andrzej Duda",
//                timeMilis = System.currentTimeMillis() - 789,
//                uid = "asdwqq",
//                respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa", 1))
//            )
//        ),
//
//        Pair(
//            "ruytuty", NewsItem(
//                comment = "Kurwa, działa !",
//                points = 10,
//                quiz = "Kotlin średni",
//                image = "https://image.shutterstock.com/image-photo/sexy-smiling-handsome-man-crossed-600w-439389757.jpg",
//                user = "Karol Zohan",
//                timeMilis = System.currentTimeMillis() - 329,
//                uid = "asdwqq",
//                respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa", 1))
//            )
//        ),
//
//        Pair(
//            "poiloili", NewsItem(
//                comment = "Nie wiem co napisać",
//                points = 10,
//                quiz = "Kotlin trudny",
//                image = "https://image.shutterstock.com/image-photo/portrait-young-handsome-man-smiling-600w-213159280.jpg",
//                user = "Kamil Tokarz",
//                timeMilis = System.currentTimeMillis() - 429,
//                uid = "asdwqq",
//                respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa", 1))
//            )
//        )



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpRecycler()
    }

    private fun setUpRecycler() {
        if (context is NewsListFragment.OnNewsInteractionListener) {
            feed_recycler.layoutManager = LinearLayoutManager(context)
            feed_recycler.adapter = NewsListRecyclerViewAdapter(
                mNewsMap,
                context as NewsListFragment.OnNewsInteractionListener
            )
        }
    }

    //OBsluga ekranu -ekran bedzie zmienial stan w zaleznsci, czy uzytkownik jest zalogowany czy nie
    override fun onStop() {
        super.onStop()
        QApp.fAuth.removeAuthStateListener(authListener)
        //usunac listenera listy newsow
    }

    override fun onStart() {
        super.onStart()
        QApp.fAuth.addAuthStateListener(authListener)
        updateCurrentUser()
        updateFeedRefEventListener()
        updateLogon()
    }

    private fun updateCurrentUser() {
        currentUser = arguments?.get(USER) as? UserItem
        if(currentUser == null){
            val firebase = QApp.fAuth.currentUser
            firebase?.let{
                currentUser = firebase.toUserItem()
            }
        }
    }

    private fun updateLogon() {
        when {
            currentUser == null -> {
                setUpViewsNotLogged()
                sign_in_button.setOnClickListener {
                    (activity as OnLogChangeListener).onLogIn()
                    loader_profil.visibility = View.VISIBLE
                }
            }
            currentUser != null -> setUpViewsLogged()
        }
        updateDebugFabLogout()
    }

    @SuppressLint("RestrictedApi")
    private fun updateDebugFabLogout() {
        //TO DODAlem
        if(BuildConfig.DEBUG){
            val visibility = (currentUser !=null && context is MainActivity)
            fab_debug_logout.visibility = if (visibility) View.VISIBLE else View.GONE
            fab_debug_logout.setOnClickListener{
                currentUser = null
                (activity as OnLogChangeListener).onLogOut()
                updateLogon()
            }
        }
    }

    //Fragment nie zalogowany
    private fun setUpViewsNotLogged() {
        login_layout.visibility = View.VISIBLE
        feed_recycler.visibility = View.GONE
        respects.text = 0.toString()
        points.text = 0.toString()
        circleProfileImage.setImageDrawable(QApp.res.getDrawable(R.drawable.ic_anonym_face, null))
        collapsing_toolbar.title = QApp.res.getString(R.string.anonym_name)
    }

    //Fragment zalogowany
    private fun setUpViewsLogged() {
        login_layout.visibility = View.GONE
        feed_recycler.visibility = View.VISIBLE
        //Imie i obrazek
        setUpUserData()

    }

    private fun setUpUserData() {
        collapsing_toolbar.title = currentUser!!.name
        Glide.with(this@ProfileFragment)
            .load(currentUser?.url)
            .into(circleProfileImage)
    }

    interface OnLogChangeListener{
        fun onLogOut()
        fun onLogIn()
    }


    companion object {
        const val USER = "USER"
        fun newInstance(user: UserItem): ProfileFragment {

            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable(USER, user)
            fragment.arguments = bundle
            return fragment
        }
    }
}

