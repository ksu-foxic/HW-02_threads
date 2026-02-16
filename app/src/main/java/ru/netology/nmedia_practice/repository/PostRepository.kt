package ru.netology.nmedia_practice.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia_practice.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun send(id: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post
}