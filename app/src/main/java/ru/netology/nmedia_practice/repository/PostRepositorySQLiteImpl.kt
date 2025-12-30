package ru.netology.nmedia_practice.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia_practice.dto.Post
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia_practice.dao.PostDao
import java.lang.reflect.Type

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data


    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countLikes = if (it.likedByMe) it.countLikes - 1 else it.countLikes + 1
            )
        }
        data.value = posts
    }

    override fun send(id: Long) {
        dao.send(id)
        posts = dao.getAll()
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }
}
