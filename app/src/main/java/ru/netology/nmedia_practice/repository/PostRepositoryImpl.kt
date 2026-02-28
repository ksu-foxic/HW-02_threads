package ru.netology.nmedia_practice.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import ru.netology.nmedia_practice.dto.Post
import java.util.concurrent.TimeUnit
class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val postType = object : TypeToken<List<Post>>() {}.type
    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999"
        val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetCallBack<List<Post>>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val posts =
                            response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(posts, postType))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.UnitCallBack) {
        val request = Request.Builder()
            .post("".toRequestBody())
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    } finally {
                        response.close()
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun unlikeByIdAsync(id: Long, callback: PostRepository.UnitCallBack) {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun send(id: Long) {
        TODO("Not yet implemented")
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.UnitCallBack) {
        val request = Request.Builder()
            .delete()
            .url("$BASE_URL/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            // Если сервер вернул ошибку - выбрасываем исключение
                            throw IOException("HTTP ${response.code}: ${response.message}")
                        }
                    } catch (e: Exception) {
                        callback.onError(e)
                    } finally {
                        response.close() // Закрываем response
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetCallBack<Post>) {
        val request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("$BASE_URL/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{
                override fun onResponse(call: Call, response: Response) {
                    try{
                        val jsonResponse = response?.body?.string() ?: throw RuntimeException("text is null")
                        Log.d("PostRepository", "Response: $jsonResponse")
                        val savePost = gson.fromJson(jsonResponse, Post::class.java)
                        callback.onSuccess(savePost)
                    } catch (e: Exception) {
                        Log.e("PostRepository", "Error parsing response", e)
                        callback.onError(e)
                    } finally {
                        response.close() // Закрываем response
                    }
                }
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("PostRepository", "Network failure", e)
                    callback.onError(e)
                }
            })
    }
}


// не используем
//    override fun getAll(): List<Post> {
//        val request = Request.Builder()
//            .url("$BASE_URL/api/slow/posts")
//            .build()
//
//        val call = client.newCall(request)
//        val response = call.execute()
//        val jsonResponse = response.body.string()
//        return gson.fromJson(jsonResponse, postType)
//    }


// не используем
//    override fun likeById(id: Long) {
//        val request = Request.Builder()
//            .post("".toRequestBody())
//            .url("$BASE_URL/api/slow/posts/$id/likes")
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) {
//                throw IOException("Failed to like: ${response.code}")
//            }
//        }
//    }


// не используем
//    override fun unlikeById(id: Long) {
//        val request = Request.Builder()
//            .delete()
//            .url("$BASE_URL/api/slow/posts/$id/likes")
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) {
//                throw IOException("Failed to unlike: ${response.code}")
//            }
//        }
//    }

// не используем
//    override fun removeById(id: Long) {
//        val request = Request.Builder()
//            .delete()
//            .url("$BASE_URL/api/slow/posts/$id")
//            .build()
//
//        val call = client.newCall(request)
//        call.execute()
//    }

//не используем
//    override fun save(post: Post): Post {
//        val request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("$BASE_URL/api/slow/posts")
//            .build()
//
//        val call = client.newCall(request)
//        val response = call.execute()
//
//        val jsonResponse = response.body.string()
//        return gson.fromJson(jsonResponse, Post::class.java)
//    }


