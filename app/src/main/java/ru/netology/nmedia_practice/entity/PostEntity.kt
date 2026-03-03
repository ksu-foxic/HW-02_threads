package ru.netology.nmedia_practice.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia_practice.dto.Attachment
import ru.netology.nmedia_practice.dto.AttachmentType
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
    val videoUrl: String? = null,
    val attachmentUrl: String? = null,
    val attachmentDescription: String? = null,
    val attachmentType: String? = null
) {
    fun toDto(): Post {
        val attachment = if (attachmentUrl != null && attachmentType != null) {
            Attachment(
                url = attachmentUrl,
                description = attachmentDescription,
                type = AttachmentType.valueOf(attachmentType)
            )
        } else null

        return Post(
            id = id,
            author = author,
            published = published,
            content = content,
            likedByMe = likedByMe,
            likes = likes,
            send = send,
            countSend = countSend,
            countView = countView,
            authorAvatar = authorAvatar,
            videoUrl = videoUrl,
            attachment = attachment  // передаем объект Attachment, а не отдельные поля
        )
    }
    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id, post.author, post.published, post.content, post.likedByMe, post.likes, post.send, post.countSend, post.countView, post.authorAvatar,post.videoUrl,
            post.attachment?.url, post.attachment?.description, post.attachment?.type?.name
        )
    }
}