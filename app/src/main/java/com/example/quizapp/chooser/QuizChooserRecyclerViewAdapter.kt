package com.example.quizapp.chooser

import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import org.w3c.dom.Text

class QuizChooserRecyclerViewAdapter(private val quizzesMap: HashMap<String, QuizItem>,
                                     private val onStartquizListener:QuizChooserFragment.OnStartQuizListener): RecyclerView.Adapter<QuizChooserRecyclerViewAdapter.ViewHolder>() {
   // class ViewHolder(val mView: View): RecyclerView.ViewHolder(mView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_quizitem, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = quizzesMap.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sorted = quizzesMap.values.toList().sortedBy { quizItem -> (quizItem.level.ordinal + quizItem.lang.ordinal *10) }
        holder.mItem = sorted[position]

        holder.levelImageView.setImageResource(sorted[position].level.image)
        holder.langImageView.setImageResource(sorted[position].lang.image)
        holder.quizTitle.text = getDoubleLineQuizTitle(sorted, position)



    holder.mView.setOnClickListener{
        onStartquizListener.onStartQuizSelected(holder.mItem ,getSingleLineQuizTitle(sorted,position))
    }
    }

    private fun getSingleLineQuizTitle(sorted: List<QuizItem>, position: Int)
            ="${sorted[position].lang.getString()} \n ${sorted[position].level.getString()}"

    private fun getDoubleLineQuizTitle(sorted: List<QuizItem>, position: Int)
            ="${sorted[position].lang.getString()} \n ${sorted[position].level.getString()}"



    inner class ViewHolder(val mView: View): RecyclerView.ViewHolder(mView){
        val levelImageView = mView.findViewById<View>(R.id.levelImageView) as ImageView
        val langImageView = mView.findViewById<View>(R.id.langImageView) as ImageView
        val quizTitle = mView.findViewById<View>(R.id.quizTitle) as TextView

        lateinit var mItem: QuizItem

    }
}