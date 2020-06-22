package com.example.quizapp.chooser

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_quizitem_list.*
//import com.example.quizapp.chooser.layoutManager as layoutManager

//private var View.adapter: QuizChooserRecyclerViewAdapter?
//    get() = null
//    set(value) = TODO()

//private var View.layoutManager: GridLayoutManager?
//get()= null
//set(value)= TODO()


class QuizChooserFragment : Fragment() {

    private val quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes")

    private lateinit var onStartQuizListener: OnStartQuizListener

    private val quizzesMap: HashMap<String, QuizItem> = HashMap()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnStartQuizListener){
            onStartQuizListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quizitem_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpRecyclerView()
        //TODO doimplementować makiety
        setCommunication()
    }

    private fun setCommunication() {
        //Pytania do quizu
       loader_quiz.visibility = View.VISIBLE
        quizzesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                loader_quiz.visibility = View.GONE
                for(message in p0.children){
                    val quizItem = message.getValue(QuizItem::class.java)!!
                    quizzesMap.put(message.key!!,quizItem)
                }
                quest_item_list.adapter?.notifyDataSetChanged()
            }


        })

    }

    private fun setUpRecyclerView() {

        quest_item_list.layoutManager = GridLayoutManager(context, COLUMN_COUNT)
        quest_item_list.adapter = QuizChooserRecyclerViewAdapter(quizzesMap, onStartQuizListener)


    }
    interface OnStartQuizListener{
        fun onStartQuizSelected(quiz: QuizItem,string:String)
    }
    companion object {
        private const val COLUMN_COUNT = 3
    }

}