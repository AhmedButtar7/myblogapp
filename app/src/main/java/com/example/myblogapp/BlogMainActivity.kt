package com.example.myblogapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myblogapp.adapters.BlogAdapter
import com.example.myblogapp.database.AppDatabase
import com.example.myblogapp.models.Blog
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class BlogMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var blogRecyclerView: RecyclerView
    private lateinit var createBlogButton: Button
    private lateinit var blogAdapter: BlogAdapter
    private val blogList = mutableListOf<Blog>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_main)

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        blogRecyclerView = findViewById(R.id.blogRecyclerView)
        createBlogButton = findViewById(R.id.createBlogButton)

        // Set NavigationView Listener
        navigationView.setNavigationItemSelectedListener(this)

        // Setup RecyclerView
        blogRecyclerView.layoutManager = LinearLayoutManager(this)
        blogAdapter = BlogAdapter(blogList) { blog ->
            val intent = Intent(this, BlogDetailActivity::class.java)
            intent.putExtra("BLOG", blog)
            startActivity(intent)
        }
        blogRecyclerView.adapter = blogAdapter

        // Setup Create Blog Button
        createBlogButton.setOnClickListener {
            startActivity(Intent(this, CreateBlogActivity::class.java))
        }

        // Load data
        loadUserData()
        loadBlogs()
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "") ?: ""

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Invalid login. Please try again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            val userProfile = AppDatabase.getDatabase(applicationContext).userProfileDao().getUserProfile(userEmail)
            if (userProfile != null) {
                val headerView = navigationView.getHeaderView(0)
                val profileImageView: ImageView = headerView.findViewById(R.id.navHeaderImageView)
                val usernameTextView: TextView = headerView.findViewById(R.id.navHeaderUsernameTextView)
                val emailTextView: TextView = headerView.findViewById(R.id.navHeaderEmailTextView)

                usernameTextView.text = userProfile.username
                emailTextView.text = userProfile.email
                userProfile.profileImageUri?.let {
                    profileImageView.setImageURI(Uri.parse(it))
                }
            } else {
                Toast.makeText(this@BlogMainActivity, "User profile not found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Call `loadUserData()` when the activity resumes to reflect updates.
    override fun onResume() {
        super.onResume()
        loadUserData()
    }





    private fun loadBlogs() {
        val blogDao = AppDatabase.getDatabase(applicationContext).blogDao()
        lifecycleScope.launch {
            val blogs = blogDao.getAllBlogs()
            if (blogs.isNotEmpty()) {
                blogList.clear()
                blogList.addAll(blogs)
                blogAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@BlogMainActivity, "No blogs found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_blogs -> startActivity(Intent(this, MyBlogsActivity::class.java))
            R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.nav_logout -> logoutUser()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_UPDATE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload the sidebar data to reflect updated profile info
            loadUserData()
        }
    }
    companion object {
        private const val PROFILE_UPDATE_REQUEST_CODE = 1001
    }

}
