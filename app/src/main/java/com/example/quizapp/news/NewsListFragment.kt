package com.example.quizapp.news

import android.content.Context
import android.os.Bundle
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.QApp
import com.example.quizapp.R
import com.example.quizapp.profile.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_newsitem_list.*

class NewsListFragment: Fragment() {


    private lateinit var onNewsInteractionListener: OnNewsInteractionListener

    private val mNewsMap: HashMap<String,NewsItem> = hashMapOf()

    val feedsRef = FirebaseDatabase.getInstance().getReference("feeds")

    val authListener:FirebaseAuth.AuthStateListener by lazy{
        FirebaseAuth.AuthStateListener {
            firebaseAuth ->
            if(firebaseAuth.currentUser != null){
                feed_item_list.adapter?.notifyDataSetChanged()
            }
        }
    }

    lateinit var feedChangeListener:ValueEventListener

    val eventListener : ValueEventListener by lazy {
        object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                onUpdateRecyclerAdapter(p0)
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNewsInteractionListener){
            onNewsInteractionListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_newsitem_list,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loader_news.visibility = View.VISIBLE

       // onUpdateAdapter()
//    mNewsMap.apply {
//        put("asd",NewsItem(
//            comment = "Super",
//            points = 10,
//            quiz= "Kotlin łatwy",
//            image = "https://cdn.stocksnap.io/img-thumbs/960w/guy-man_6R7KCXBEEE.jpg",
//             user = "Kamil Tokarz",
//            timeMilis = System.currentTimeMillis() - 1000,
//            uid = "asdwqq",
//            respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa",1))
//        ))
//        put("jmhgjghk",NewsItem(
//            comment = "Walcze z ostrym cieniem mgły",
//            points = 10,
//            quiz= "Kotlin trudny",
//            image = "https://gfx.wiadomosci.radiozet.pl/var/radiozetwiadomosci/storage/images/polska/polityka/andrzej-duda-rapuje-w-hot16challenge2.-premiera-morawieckiego/7448954-1-pol-PL/Hot16Challenge2-w-wykonaniu-Andrzeja-Dudy.-Nominowal-premiera-Morawieckiego_article.png",
//            user = "Andrzej Duda",
//            timeMilis = System.currentTimeMillis() - 789,
//            uid = "asdwqq",
//            respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa",1))
//        ))
//
//        put("ruytuty",NewsItem(
//            comment = "Kurwa, działa !",
//            points = 10,
//            quiz= "Kotlin średni",
//            image = "https://image.shutterstock.com/image-photo/sexy-smiling-handsome-man-crossed-600w-439389757.jpg",
//            user = "Karol Zohan",
//            timeMilis = System.currentTimeMillis() - 329,
//            uid = "asdwqq",
//            respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa",1))
//        ))
//        put("poiloili",NewsItem(
//            comment = "Nie wiem co napisać",
//            points = 10,
//            quiz= "Kotlin trudny",
//            image = "https://image.shutterstock.com/image-photo/portrait-young-handsome-man-smiling-600w-213159280.jpg",
//            user = "Kamil Tokarz",
//            timeMilis = System.currentTimeMillis() - 429,
//            uid = "asdwqq",
//            respects = hashMapOf(Pair("asdwqq", 1), Pair("fsa",1))
//        ))
//    }
        feedsRef.addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot){
            loader_news.visibility = View.GONE
                onUpdateRecyclerAdapter(p0)
                feed_item_list.scheduleLayoutAnimation()
            }
        })
        setUpRecycler()
    }

    override fun onResume() {
        super.onResume()
        feedChangeListener = feedsRef.addValueEventListener(eventListener)
        QApp.fAuth.addAuthStateListener(authListener)
    }


    override fun onStop() {
        super.onStop()
        feedsRef.removeEventListener(feedChangeListener)
        QApp.fAuth.removeAuthStateListener(authListener)
    }

    private fun onUpdateRecyclerAdapter(dataSnapshot: DataSnapshot) {
        for(it in dataSnapshot.children){
            val news = it.getValue<NewsItem>(NewsItem::class.java)!!
            mNewsMap.put(it.key!!,news)
        }
        feed_item_list.adapter?.notifyDataSetChanged()

    }

    private fun setUpRecycler() {
        feed_item_list.layoutManager = LinearLayoutManager(context)
        feed_item_list.adapter = NewsListRecyclerViewAdapter(mNewsMap, onNewsInteractionListener)
    }
    interface  OnNewsInteractionListener {
        fun onUserSelected(user:UserItem, image : View)
        fun onLikeSelected(feedId: String, diff:Int)

    }
}