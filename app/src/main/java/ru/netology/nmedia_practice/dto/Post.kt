package ru.netology.nmedia_practice.dto
data class Post (
    val id: Long,
    val author: String,
    val published: Long,
    val content: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val send: Boolean = false,
    val countSend: Int = 0,
    val countView: Int = 0,
    val authorAvatar: String? = null,
    val videoUrl: String? = null
)