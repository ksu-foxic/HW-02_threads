package ru.netology.nmedia_practice.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia_practice.db.AppDb
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.repository.PostRepository
import ru.netology.nmedia_practice.repository.PostRepositoryRoomImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    countLikes = 0,
    likedByMe = false
)
class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl(AppDb.getInstance(application).postDao())

    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun likeById(id: Long) {
        repository.likeById(id)
    }
    fun send(id: Long) {
        repository.send(id)
    }
    fun removeById(id: Long) {
        repository.removeById(id)
    }
    fun save(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content != text) {
                repository.save(it.copy(content = text))
            }
        }
        edited.value = empty
    }
    fun edit(post: Post) {
        edited.value = post
    }
}