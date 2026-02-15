package ru.netology.nmedia_practice.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.dao.PostDao
import ru.netology.nmedia_practice.entity.PostEntity

class PostRepositoryRoomImpl(
    private val dao: PostDao
) : PostRepository {

    override fun getAll() = dao.getAll().map {list-> list.map { it.toDto() } }

    override fun save(post: Post) = dao.save(PostEntity.fromDto(post))

    override fun likeById(id: Long) = dao.likeById(id)

    override fun send(id: Long) = dao.send(id)

    override fun removeById(id: Long) = dao.removeById(id)
}