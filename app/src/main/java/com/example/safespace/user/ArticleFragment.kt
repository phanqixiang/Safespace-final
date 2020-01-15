package com.example.safespace.user


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safespace.R
import com.example.safespace.entity.Article
import com.example.safespace.entity.ArticleAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_article.*


/**
 * A simple [Fragment] subclass.
 */
class ArticleFragment : Fragment() {

    lateinit var mDatabase: DatabaseReference
    lateinit var articleList: MutableList<Article>
    lateinit var list: RecyclerView

    val TAG = "ArticleFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articleList = mutableListOf()

        list = recyclerView

        mDatabase = FirebaseDatabase.getInstance().getReference("articles")

        mDatabase.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot!!.exists()){
                    for(h in dataSnapshot.children){
                        val article = h.getValue(Article::class.java)
                        articleList.add(article!!)
                    }

                    val adapter = ArticleAdapter(articleList)

                    val mLayoutManager = LinearLayoutManager(activity)
                    mLayoutManager.reverseLayout = true

                    list.layoutManager = mLayoutManager
                    list.scrollToPosition(articleList.size-1)

                    list.adapter = adapter
                }

            }

        })

    }
}
