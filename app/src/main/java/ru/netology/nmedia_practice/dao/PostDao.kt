package ru.netology.nmedia_practice.dao

import ru.netology.nmedia_practice.dto.Post
interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun send(id: Long)
    fun likeById(id: Long)
    fun removeById(id: Long)
}
