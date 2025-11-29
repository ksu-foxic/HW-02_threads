package ru.netology.nmedia_practice.dto
data class Post (
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean,
    val countLikes: Long,
    val send: Boolean,
    val countSend: Long,
    val countView: Long
)