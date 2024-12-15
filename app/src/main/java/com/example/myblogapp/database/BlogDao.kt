package com.example.myblogapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.myblogapp.models.Blog

@Dao
interface BlogDao {
    @Insert
    suspend fun insertBlog(blog: Blog)

    @Query("SELECT * FROM blogs WHERE authorEmail = :authorEmail")
    suspend fun getBlogsByAuthor(authorEmail: String): List<Blog>

    @Query("SELECT * FROM blogs")
    suspend fun getAllBlogs(): List<Blog>

    @Delete
    suspend fun deleteBlog(blog: Blog)
}
