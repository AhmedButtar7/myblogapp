package com.example.myblogapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myblogapp.database.AppDatabase
import com.example.myblogapp.models.Blog
import kotlinx.coroutines.launch

class CreateBlogActivity : AppCompatActivity() {

    private lateinit var addBlogButton: Button
    private lateinit var blogTitleEditText: EditText
    private lateinit var blogDescriptionEditText: EditText
    private lateinit var blogContentEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var blogImageView: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_blog)

        addBlogButton = findViewById(R.id.addBlogButton)
        blogTitleEditText = findViewById(R.id.blogTitleEditText)
        blogDescriptionEditText = findViewById(R.id.blogDescriptionEditText)
        blogContentEditText = findViewById(R.id.blogContentEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        blogImageView = findViewById(R.id.blogImageView)

        addBlogButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("userEmail", "") ?: ""
            val blogDao = AppDatabase.getDatabase(applicationContext).blogDao()

            lifecycleScope.launch {
                val userProfile = AppDatabase.getDatabase(applicationContext).userProfileDao().getUserProfile(userEmail)
                if (userProfile != null) {
                    val blog = Blog(
                        title = blogTitleEditText.text.toString(),
                        description = blogDescriptionEditText.text.toString(),
                        content = blogContentEditText.text.toString(),
                        authorName = userProfile.username,
                        authorAbout = "About ${userProfile.username}",
                        authorEmail = userEmail,
                        imageUrl = selectedImageUri.toString(),
                        authorImageUrl = userProfile.profileImageUri
                    )
                    blogDao.insertBlog(blog)
                    Toast.makeText(this@CreateBlogActivity, "Blog added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateBlogActivity, "Unable to fetch user details.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                blogImageView.setImageURI(uri)
            }
        }
    }

    private fun saveBlog(blog: Blog) {
        val blogDao = AppDatabase.getDatabase(applicationContext).blogDao()
        lifecycleScope.launch {
            blogDao.insertBlog(blog)
            addBlogToMainActivity(blog)
            Toast.makeText(this@CreateBlogActivity, "Blog added successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun addBlogToMainActivity(blog: Blog) {
        val intent = Intent(this, BlogMainActivity::class.java)
        intent.putExtra("NEW_BLOG", blog)
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}
