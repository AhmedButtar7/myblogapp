package com.example.myblogapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myblogapp.models.Blog

class BlogDetailActivity : AppCompatActivity() {

    private lateinit var blogImageView: ImageView
    private lateinit var blogTitleTextView: TextView
    private lateinit var blogContentTextView: TextView
    private lateinit var authorImageView: ImageView
    private lateinit var authorNameTextView: TextView
    private lateinit var authorAboutTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detail)

        // Initialize views using findViewById
        blogImageView = findViewById(R.id.blogImageView)
        blogTitleTextView = findViewById(R.id.blogTitleTextView)
        blogContentTextView = findViewById(R.id.blogContentTextView)
        authorImageView = findViewById(R.id.authorImageView)
        authorNameTextView = findViewById(R.id.authorNameTextView)
        authorAboutTextView = findViewById(R.id.authorAboutTextView)

        // Get the blog object from the intent
        val blog = intent.getParcelableExtra<Blog>("BLOG")

        // Bind data to views
        blog?.let {
            blogTitleTextView.text = it.title
            blogContentTextView.text = it.content
            authorNameTextView.text = it.authorName
            authorAboutTextView.text = it.authorAbout

            // Load images using Glide
            Glide.with(this)
                .load(it.imageUrl) // assuming Blog model has imageUrl property
                .placeholder(R.drawable.ic_blog_image)
                .into(blogImageView)

            Glide.with(this)
                .load(it.authorImageUrl) // assuming Blog model has authorImageUrl property
                .placeholder(R.drawable.ic_user)
                .into(authorImageView)
        }
    }
}
