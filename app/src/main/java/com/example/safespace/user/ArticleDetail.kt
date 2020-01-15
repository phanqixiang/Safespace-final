package com.example.safespace.user

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.example.safespace.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_article_detail.*

class ArticleDetail : AppCompatActivity() {

    lateinit var fullTitle: TextView
    lateinit var fullDesc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)


        var intent= intent

        val title = intent.getStringExtra("Title")
        val desc = intent.getStringExtra("Description")
        val image = intent.getStringExtra("Image")

        fullTitle = findViewById(R.id.textView6)
        fullDesc= findViewById(R.id.textView7)

        fullTitle.text = title
        fullDesc.text= desc

        Picasso.get().load(image).into(fullImage)
    }




}
