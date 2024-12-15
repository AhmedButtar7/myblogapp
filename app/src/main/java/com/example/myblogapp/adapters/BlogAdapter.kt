package com.example.myblogapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myblogapp.R
import com.example.myblogapp.models.Blog

class BlogAdapter(
    private val blogs: List<Blog>,
    private val onItemClick: (Blog) -> Unit
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val blogImageView: ImageView = itemView.findViewById(R.id.blogImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.blogTitleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.blogDescriptionTextView)

        fun bind(blog: Blog) {
            titleTextView.text = blog.title
            descriptionTextView.text = blog.description
            blogImageView.setImageURI(Uri.parse(blog.imageUrl))
            itemView.setOnClickListener { onItemClick(blog) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_blog, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount(): Int = blogs.size
}
