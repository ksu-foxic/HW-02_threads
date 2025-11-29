package ru.netology.nmedia_practice.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia_practice.activity.MainActivity
import ru.netology.nmedia_practice.repository.PostRepository
import ru.netology.nmedia_practice.repository.PostRepositoryInMemoryImpl

class PostViewModel: ViewModel (){
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    fun likeById(id: Long) {
        repository.likeById(id)
    }
    fun send(id:Long) {
        repository.send(id)
    }
}