package ru.netology.nmedia_practice.dto
data class Post (
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean = false,
    val countLikes: Long = 0,
    val send: Boolean = false,
    val countSend: Long = 0,
    val countView: Long = 0
)