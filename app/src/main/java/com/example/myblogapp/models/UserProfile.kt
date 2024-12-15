package com.example.myblogapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val email: String,
    val username: String,
    val profileImageUri: String? = null
)
