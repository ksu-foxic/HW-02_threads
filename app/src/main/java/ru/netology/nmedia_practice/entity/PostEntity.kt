package ru.netology.nmedia_practice.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia_practice.dto.Post

@Entity
class PostEntity (
    @PrimaryKey(autoGenerate = true)
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
) {
    fun toDto() = Post(
        id, author, published, content, likedByMe, likes, send, countSend, countView, videoUrl
    )
    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id, post.author, post.published, post.content, post.likedByMe, post.likes, post.send, post.countSend, post.countView, post.videoUrl
        )
    }
}