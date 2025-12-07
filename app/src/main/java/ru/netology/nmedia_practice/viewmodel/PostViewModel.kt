package ru.netology.nmedia_practice.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia_practice.activity.MainActivity
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.repository.PostRepository
import ru.netology.nmedia_practice.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
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
        edited.value?.let { post ->
            val trim: String = content.trim()
            if (trim != post.content) {
                repository.save(post.copy(content = trim))
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun canselEdit() {
        edited.value = empty
    }

}