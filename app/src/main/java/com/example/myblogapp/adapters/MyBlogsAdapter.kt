package com.example.myblogapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myblogapp.R
import com.example.myblogapp.models.Blog

class MyBlogsAdapter(
    private val blogs: List<Blog>,
    private val onEditClick: (Blog) -> Unit,
    private val onDeleteClick: (Blog) -> Unit
) : RecyclerView.Adapter<MyBlogsAdapter.MyBlogViewHolder>() {

    inner class MyBlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val myBlogTitleTextView: TextView = itemView.findViewById(R.id.myBlogTitleTextView)
        private val editBlogButton: Button = itemView.findViewById(R.id.editBlogButton)
        private val deleteBlogButton: Button = itemView.findViewById(R.id.deleteBlogButton)

        fun bind(blog: Blog) {
            myBlogTitleTextView.text = blog.title
            editBlogButton.setOnClickListener { onEditClick(blog) }
            deleteBlogButton.setOnClickListener { onDeleteClick(blog) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_blog, parent, false)
        return MyBlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBlogViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount(): Int = blogs.size
}
