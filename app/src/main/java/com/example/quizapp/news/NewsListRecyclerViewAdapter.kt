package com.example.quizapp.news

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quizapp.QApp
import com.example.quizapp.profile.UserItem
import com.google.firebase.auth.FirebaseAuth


class NewsListRecyclerViewAdapter(private val mNewsMap: HashMap<String,NewsItem>, private val onNewsInteractionListener: NewsListFragment.OnNewsInteractionListener) : RecyclerView.Adapter<NewsListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_newsitem,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sortedList = mNewsMap.toList().sortedWith(Comparator({o1, o2 -> if (o1.second.timeMilis < o2.second.timeMilis) 1 else -1}))
        val second = sortedList[position].second

        holder.mItem = second
            holder.name.text = second.user
            holder.time.text = getElapserTimeMinutesFromString(second.timeMilis)
            holder.quizTitle.text = second.quiz
            holder.comment.text = second.comment
            holder.comment.visibility = getCommentVisibility(second)
            holder.points.text = second.points.toString()
            holder.respects.text= countRespects(second)
            Glide.with(holder.mView.context)
                .load(second.image)
                .into(holder.profile)

        //Lajki - zmieniamy jego aktualny stan obrazka
        holder.respects.setOnClickListener { holder.likesImage.isChecked = !holder.likesImage.isChecked }
        holder.name.setOnClickListener { _ -> onNewsInteractionListener.onUserSelected(UserItem(second.user,second.image,second.uid), holder.profile)}
        holder.profile.setOnClickListener { _ -> onNewsInteractionListener.onUserSelected(UserItem(second.user,second.image,second.uid), holder.profile) }

        //zabezpieczenie przed zapetleniem likesImage
        holder.likesImage.setOnCheckedChangeListener(null)
        holder.likesImage.isChecked = getLikeChecked(second)
        holder.likesImage.setOnCheckedChangeListener(onCheckedChangeListener(sortedList[position].first))
    }

    private fun onCheckedChangeListener(feedId: String): (CompoundButton, Boolean)  -> Unit ={
        compoundButton, isChecked ->
        if(FirebaseAuth.getInstance().currentUser !=null){
            val diff = if (isChecked) 1 else 0
            onNewsInteractionListener.onLikeSelected(feedId = feedId,diff = diff)

        }else{
            compoundButton.isChecked = false
            Toast.makeText(compoundButton.context, QApp.res.getString(R.string.not_logged_like), Toast.LENGTH_SHORT).show()
        }
    }



    private fun getCommentVisibility(second: NewsItem): Int {
        return if (second.comment.isNotEmpty()) {
            View.VISIBLE
        }else{
            View.GONE
        }


    }

    private fun getLikeChecked(second: NewsItem): Boolean = FirebaseAuth.getInstance().currentUser !=null && second.respects[FirebaseAuth.getInstance().currentUser?.uid] == 1




    private fun countRespects(second: NewsItem): String {
       return second.respects.values.count{ it ==1}.plus (1).toString()

    }

    private fun getElapserTimeMinutesFromString(timeMilis: Long): String {
        val elapsedTimeSec = (System.currentTimeMillis()- timeMilis) / 1000
        val format = String.format("%%0%dd",2)
        return when {
            (elapsedTimeSec / 3600 > 24) ->{
                val days = elapsedTimeSec /(60 *60 *24)
                String.format(format,days) + "d"
            }
            (elapsedTimeSec / 60 >60) ->{
                val hours = elapsedTimeSec / (60 *60)
                String.format(format,hours) +"h"
            }
            else ->{
                String.format(format, elapsedTimeSec / 60 ) +"m"
            }
        }

    }


    override fun getItemCount() = mNewsMap.size


inner class ViewHolder(val mView : View):RecyclerView.ViewHolder(mView){
    var mItem: NewsItem? = null

   val name  = mView.findViewById<TextView>(R.id.name)!!
   val time  = mView.findViewById<TextView>(R.id.time)!!
   val quizTitle    = mView.findViewById<TextView>(R.id.quizTitle)!!
   val comment   = mView.findViewById<TextView>(R.id.comment)!!
   val points   = mView.findViewById<TextView>(R.id.pointsText)!!
   val respects   = mView.findViewById<TextView>(R.id.respects)!!
   val likesImage     = mView.findViewById<CheckBox>(R.id.likesImage)!!
   val profile   = mView.findViewById<BootstrapCircleThumbnail>(R.id.circleImageProfile)!!
}
}


