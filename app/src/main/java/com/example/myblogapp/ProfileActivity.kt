package com.example.myblogapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myblogapp.database.AppDatabase
import com.example.myblogapp.models.UserProfile
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var changePictureButton: Button
    private lateinit var saveProfileButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var profileNameEditText: EditText
    private lateinit var profileAboutEditText: EditText
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        changePictureButton = findViewById(R.id.changePictureButton)
        saveProfileButton = findViewById(R.id.saveProfileButton)
        profileImageView = findViewById(R.id.profileImageView)
        profileNameEditText = findViewById(R.id.profileNameEditText)
        profileAboutEditText = findViewById(R.id.profileAboutEditText)

        loadUserData()

        changePictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        saveProfileButton.setOnClickListener {
            saveProfile()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                profileImageView.setImageURI(uri)
            }
        }
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "") ?: ""
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        lifecycleScope.launch {
            val userProfile = AppDatabase.getDatabase(applicationContext).userProfileDao().getUserProfile(userEmail)
            if (userProfile != null) {
                profileNameEditText.setText(userProfile.username)
                profileAboutEditText.setText(userProfile.profileImageUri) // Assuming this is "About"
                userProfile.profileImageUri?.let {
                    profileImageView.setImageURI(Uri.parse(it))
                }
            } else {
                Toast.makeText(this@ProfileActivity, "User profile not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfile() {
        val sharedPreferences = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "") ?: ""

        lifecycleScope.launch {
            val userProfileDao = AppDatabase.getDatabase(applicationContext).userProfileDao()
            val userProfile = UserProfile(
                email = userEmail,
                username = profileNameEditText.text.toString(),
                profileImageUri = selectedImageUri?.toString()
            )
            userProfileDao.insertUserProfile(userProfile)
            Toast.makeText(this@ProfileActivity, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Call `loadUserData()` when the profile activity finishes
    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }


    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}
