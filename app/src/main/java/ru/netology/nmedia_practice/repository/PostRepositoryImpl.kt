package ru.netology.nmedia_practice.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import ru.netology.nmedia_practice.dto.Post
import java.util.concurrent.TimeUnit

class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    private val postType = object: TypeToken<List<Post>>(){}.type

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        val jsonResponse = response.body.string()

        return gson.fromJson(jsonResponse, postType)
    }

    override fun likeById(id: Long) {
        val request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to like: ${response.code}")
            }
        }
    }

    override fun unlikeById(id: Long) {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to unlike: ${response.code}")
            }
        }
    }

    override fun send(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id")
            .build()

        val call = client.newCall(request)
        call.execute()
    }

    override fun save(post: Post): Post {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        val jsonResponse = response.body.string()
        Log.d("PostRepository", "Server response: $jsonResponse")

        return gson.fromJson(jsonResponse, Post::class.java)
        Log.d("PostRepository", "Parsed savedPost.id = ${post.id}")
    }
}