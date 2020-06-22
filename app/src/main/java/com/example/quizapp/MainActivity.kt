package com.example.quizapp


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.quizapp.chooser.QuizChooserFragment
import com.example.quizapp.chooser.QuizItem
import com.example.quizapp.news.NewsItem
import com.example.quizapp.news.NewsListFragment
import com.example.quizapp.profile.OtherProfileActivity
import com.example.quizapp.profile.ProfileFragment
import com.example.quizapp.profile.UserItem
import com.example.quizapp.quiz.QuestionItem
import com.example.quizapp.quiz.QuizActivity
import com.example.quizapp.summary.QuizSummaryActivity
import com.example.quizapp.summary.QuizSummaryActivity.Companion.NEW_FEED
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_newsitem_list.*
import kotlinx.android.synthetic.main.fragment_quizitem_list.*


// Główna aktywność app. ViewPager - obsługa gestami i BottomNavigationViewe

@Suppress("DEPRECATION")
class MainActivity : BaseActivity()
    ,QuizChooserFragment.OnStartQuizListener,
    NewsListFragment.OnNewsInteractionListener,
    ProfileFragment.OnLogChangeListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        val options = FirebaseOptions.Builder()
            .setApplicationId("APP ID") // Required for Analytics.
            .setProjectId("PROJECT ID") // Required for Firebase Installations.
            .setApiKey("GOOGLE API KEY") // Required for Auth.
            .build()
        FirebaseApp.initializeApp(this, options, "FIREBASE APP NAME")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewPager()

    }
    //region Ustawienia vPager i BotNav

    private fun setViewPager() {
        //val viewPager: ViewPager = findViewById(R.id.viewpager)
       // val navigacja: BottomNavigationView = findViewById(R.id.navigation)
        viewpager.adapter = getFragmentPagerAdapter()
        navigation.setOnNavigationItemSelectedListener(getBottomNavigationItemSelectedListener())
        viewpager.addOnPageChangeListener(getOnPageChangeListener())
        viewpager.offscreenPageLimit = 2

    }
    private fun getFragmentPagerAdapter() =
        object: FragmentPagerAdapter(supportFragmentManager){
            override fun getItem(position: Int) = when (position){
                FEED_ID -> NewsListFragment()
                CHOOSER_ID ->  QuizChooserFragment()
                //TODO nie instancjonowaćtego tak gdy będzie internet
                PROFILE_ID -> ProfileFragment()

                else ->{
                    Log.wtf("Fragment out of bounds","How Came!?")
                    Fragment()
                }
            }

            override fun getCount() = 3



        }

    private fun getBottomNavigationItemSelectedListener() =


        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId){


                R.id.navigation_home -> {
                    val view: ViewPager = findViewById(R.id.viewpager)
                    view.currentItem = 0
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_dashboard -> {
                    val view: ViewPager = findViewById(R.id.viewpager)
                    view.currentItem = 1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    val view: ViewPager = findViewById(R.id.viewpager)
                    view.currentItem = 2
                    return@OnNavigationItemSelectedListener true
                }
                else -> return@OnNavigationItemSelectedListener false
            }
        }
    //endregion
    private fun getOnPageChangeListener() =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

               // var navigacja: BottomNavigationView = findViewById(R.id.navigation)
                navigation.menu.getItem(position).isChecked = true
            }

        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when {
                (requestCode == QUIZ_ACT_REQ_CODE) -> {

                    naviagetToSummaryActivity(data)
                }
                (requestCode == QUIZ_SUMMARY_RQCODE) ->{
                    pushNewNews(data)
                }

            }
        }
    }

    private fun pushNewNews(data: Intent?) {
    val feedItem = data!!.extras?.get(NEW_FEED) as NewsItem
        QApp.fData.getReference("feeds").push().setValue(feedItem.apply {
            uid = QApp.fUser!!.uid
            image = QApp.fUser!!.photoUrl.toString()
            user = QApp.fUser!!.displayName!!
        })
        viewpager.currentItem = 0
        getNewsListFragment().feed_item_list.smoothScrollToPosition(0)
    }

    //Przejście do podsumowania
    private fun naviagetToSummaryActivity(data: Intent?) {
    var intent = Intent(this, QuizSummaryActivity::class.java).apply {
        if(QApp.fUser !=null){

            data?.putExtra(USER_NAME, QApp.fUser?.displayName?: QApp.res.getString(R.string.anonym_name))
            data?.putExtra(USER_URL, QApp.fUser?.photoUrl.toString() )
        }
        //TODO TO MOZE BY ZLE
        data!!.extras?.let { putExtras(it) }
    }
        startActivityForResult(intent, QUIZ_SUMMARY_RQCODE)
    }







    private fun getChooserListFragment()=(supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + CHOOSER_ID)as QuizChooserFragment)
    private fun getNewsListFragment() = (supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + FEED_ID)as NewsListFragment)

    override fun onStartQuizSelected(quiz: QuizItem, name: String) {
        getChooserListFragment().loader_quiz.visibility = View.VISIBLE

        QApp.fData.getReference("questions/${quiz.questset}").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val quizset = ArrayList<QuestionItem>()
                p0.children.map { it.getValue(QuestionItem::class.java) }.mapTo(quizset){it!!}
                getChooserListFragment().loader_quiz.visibility = View.GONE
                navigateQuiz(quizset,name,quiz)
            }

        })

    }

fun navigateQuiz(quizSet:ArrayList<QuestionItem>, title: String, quiz: QuizItem){
    val intent = Intent(this, QuizActivity::class.java).apply{
        putExtra(QUIZ_SET,quizSet)
        putExtra(TITLE,title)
        putExtra(QUIZ,quiz)
    }
startActivityForResult(intent, QUIZ_ACT_REQ_CODE)
}

    override fun onUserSelected(user: UserItem, image: View) {
        val intent = Intent (this,OtherProfileActivity::class.java)
        intent.putExtra(USER_ITEM, user)
        val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, image, "circleProfileImageTransition")
        startActivity(intent,optionCompat.toBundle())
    }

    override fun onLikeSelected(feedId: String, diff: Int) {
        if(QApp.fUser !=null){
            QApp.fData.getReference("feeds/$feedId/respects").updateChildren(mapOf(Pair(QApp.fUser?.uid,diff)))
                .addOnCompleteListener {Log.d("MainActivity","Just liked $feedId,with$diff") }



                }
        }


    override fun onLogOut() {
        QApp.fAuth.signOut()
        getNewsListFragment().feed_item_list.adapter?.notifyDataSetChanged()

    }



    override fun onLogIn() {
        logIn()
    }

companion object{
    const val FEED_ID = 0
    const val CHOOSER_ID = 1
    const val PROFILE_ID = 2

    const val  QUIZ_SET = "quiz_set"
    const val TITLE = "TITLE"
    const val QUIZ = "QUIZ"

    const val USER_ITEM = "USER_ITEM"

   const val USER_NAME = "USER_NAME"
    const val USER_URL = "USER_URL"

    const val QUIZ_ACT_REQ_CODE = 487;

    const val QUIZ_SUMMARY_RQCODE = 324;
}




}
