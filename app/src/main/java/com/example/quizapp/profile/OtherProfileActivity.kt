package com.example.quizapp.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.BaseActivity
import com.example.quizapp.MainActivity
import com.example.quizapp.QApp
import com.example.quizapp.R
import com.example.quizapp.news.NewsListFragment
import kotlinx.android.synthetic.main.fragment_profile.*

class OtherProfileActivity: BaseActivity(),
NewsListFragment.OnNewsInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = intent?.extras?.get(MainActivity.USER_ITEM) as UserItem
        setContentView(R.layout.other_profile_activity)
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.layout_other_profile, ProfileFragment.newInstance(user)).commit()
    }

    override fun onStart() {
        super.onStart()
        setUpToolbar()

    }

    private fun setUpToolbar() {
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onUserSelected(user: UserItem, image: View) {
        //niedostępne na tym oknie
    }

    override fun onLikeSelected(feedId: String, diff: Int) {
        if(QApp.fUser !=null){
            QApp.fData.getReference("feeds/$feedId/respects").updateChildren(mapOf(Pair(QApp.fUser?.uid,diff)))
                .addOnCompleteListener { Log.d("MainActivity","Just liked $feedId,with$diff") }



        }
    }
    }
