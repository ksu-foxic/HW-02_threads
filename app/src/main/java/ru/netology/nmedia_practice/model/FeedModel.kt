package ru.netology.nmedia_practice.model

import ru.netology.nmedia_practice.dto.Post

data class FeedModel (
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val empty: Boolean = false
)