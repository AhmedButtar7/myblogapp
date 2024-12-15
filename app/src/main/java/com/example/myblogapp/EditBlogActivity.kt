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

class EditBlogActivity : AppCompatActivity() {

    private lateinit var updateBlogButton: Button
    private lateinit var blogTitleEditText: EditText
    private lateinit var blogDescriptionEditText: EditText
    private lateinit var blogContentEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var blogImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var blog: Blog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_blog)

        updateBlogButton = findViewById(R.id.updateBlogButton)
        blogTitleEditText = findViewById(R.id.blogTitleEditText)
        blogDescriptionEditText = findViewById(R.id.blogDescriptionEditText)
        blogContentEditText = findViewById(R.id.blogContentEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        blogImageView = findViewById(R.id.blogImageView)

        // Get the blog from the intent
        blog = intent.getParcelableExtra("BLOG")!!

        // Populate fields with existing blog data
        blogTitleEditText.setText(blog.title)
        blogDescriptionEditText.setText(blog.description)
        blogContentEditText.setText(blog.content)
        selectedImageUri = Uri.parse(blog.imageUrl)
        blogImageView.setImageURI(selectedImageUri)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        updateBlogButton.setOnClickListener {
            updateBlog()
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

    private fun updateBlog() {
        val updatedBlog = blog.copy(
            title = blogTitleEditText.text.toString(),
            description = blogDescriptionEditText.text.toString(),
            content = blogContentEditText.text.toString(),
            imageUrl = selectedImageUri.toString()
        )

        val blogDao = AppDatabase.getDatabase(applicationContext).blogDao()
        lifecycleScope.launch {
            blogDao.insertBlog(updatedBlog)  // Room uses insert with conflict strategy as update
            Toast.makeText(this@EditBlogActivity, "Blog updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}
