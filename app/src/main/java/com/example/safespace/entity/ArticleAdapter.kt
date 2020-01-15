package com.example.safespace.entity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safespace.R
import com.example.safespace.user.ArticleDetail
import com.squareup.picasso.Picasso

class ArticleAdapter(var article: MutableList<Article>): RecyclerView.Adapter<ArticleAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_view,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return article.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = article[position].title
        holder.desc.text = article[position].desc

        Picasso.get().load(article[position].image).into(holder.image)

        holder.image.setOnClickListener {
            val intent = Intent(holder.title.context, ArticleDetail::class.java)
            intent.putExtra("Title",article[position].title)
            intent.putExtra("Description",article[position].desc)
            intent.putExtra("Image",article[position].image)

            holder.title.context.startActivity(intent)
        }
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.showTitle)
        val desc: TextView = itemView.findViewById(R.id.showDesc)
        val image : ImageView = itemView.findViewById((R.id.showImage))

    }
}