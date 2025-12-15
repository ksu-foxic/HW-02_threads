package ru.netology.nmedia_practice.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia_practice.dto.Post
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PostRepositoryInFileImpl(
    private val context: Context
) : PostRepository {
    //listOf
    private var posts = getPosts()
        set(value) {
            field = value
            sync()
        }
    private var nextId = getId()
    private val data = MutableLiveData(posts)

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
        posts = posts.map {
            if (it.id != id) it else {
                it.copy(
                    send = false,
                    countSend = it.countSend + 1
                )
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(post.copy(id = nextId++, author = "me")) + posts
        } else {
            posts = posts.map {
                if (it.id == post.id) {
                    it.copy(content = post.content)
                } else {
                    it
                }
            }
        }
        data.value = posts
    }
    private fun getPosts(): List<Post> = context.filesDir.resolve(FILE_NAME)
        .takeIf { it.exists() }
        ?.inputStream()
        ?.bufferedReader()
        ?.use {
            gson.fromJson(it, postsType)
        } ?: emptyList()
    private fun getId() = (posts.maxByOrNull { it.id }?.id ?: 0L) + 1L

    private fun sync() {
        context.filesDir.resolve(FILE_NAME).outputStream().bufferedWriter()
            .use {
                it.write(gson.toJson(posts))
            }
    }
    private companion object {
        const val FILE_NAME = "posts.json"
        val gson = Gson()
        val postsType: Type = object : TypeToken<List<Post>>() {}.type
    }
}