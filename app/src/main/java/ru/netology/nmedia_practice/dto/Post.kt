package ru.netology.nmedia_practice.dto
data class Post (
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val countLikes: Int = 0,
    val send: Boolean = false,
    val countSend: Int = 0,
    val countView: Int = 0,
    val videoUrl: String? = null
)