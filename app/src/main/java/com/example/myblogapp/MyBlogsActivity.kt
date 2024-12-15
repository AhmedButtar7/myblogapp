package com.example.myblogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myblogapp.adapters.MyBlogsAdapter
import com.example.myblogapp.database.AppDatabase
import com.example.myblogapp.models.Blog
import kotlinx.coroutines.launch

class MyBlogsActivity : AppCompatActivity() {

    private lateinit var myBlogsRecyclerView: RecyclerView
    private lateinit var myBlogsAdapter: MyBlogsAdapter
    private val myBlogList = mutableListOf<Blog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_blogs)

        myBlogsRecyclerView = findViewById(R.id.myBlogsRecyclerView)
        myBlogsRecyclerView.layoutManager = LinearLayoutManager(this)
        myBlogsAdapter = MyBlogsAdapter(myBlogList, { blog -> editBlog(blog) }, { blog -> deleteBlog(blog) })
        myBlogsRecyclerView.adapter = myBlogsAdapter

        loadMyBlogs()
    }

    private fun loadMyBlogs() {
        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "") ?: ""

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User not logged in. Redirecting to login.", Toast.LENGTH_SHORT).show()
            finish() // Redirect to login if needed
            return
        }

        lifecycleScope.launch {
            val blogDao = AppDatabase.getDatabase(applicationContext).blogDao()
            val blogs = blogDao.getBlogsByAuthor(userEmail)
            if (blogs.isNotEmpty()) {
                myBlogList.clear()
                myBlogList.addAll(blogs)
                myBlogsAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@MyBlogsActivity, "No blogs found for this user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editBlog(blog: Blog) {
        // Start EditBlogActivity with the selected blog
        val intent = Intent(this, EditBlogActivity::class.java)
        intent.putExtra("BLOG", blog)
        startActivity(intent)
    }

    private fun deleteBlog(blog: Blog) {
        lifecycleScope.launch {
            val blogDao = AppDatabase.getDatabase(applicationContext).blogDao()
            blogDao.deleteBlog(blog)
            myBlogList.remove(blog)
            myBlogsAdapter.notifyDataSetChanged()
            Toast.makeText(this@MyBlogsActivity, "Blog deleted successfully.", Toast.LENGTH_SHORT).show()
        }
    }
}
