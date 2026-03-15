package ru.netology.nmedia_practice.repository

import android.util.Log
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia_practice.api.PostApi
import ru.netology.nmedia_practice.dto.Post
import kotlin.collections.orEmpty

class PostRepositoryImpl : PostRepository {

    override fun getAll(callback: PostRepository.GetCallBack<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>,
                    response: Response<List<Post>>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body().orEmpty())
                    } else {
//                        callback.onError(RuntimeException(response.errorBody()?.string().orEmpty()))
                        callback.onError(IOException("SERVER_ERROR"))
                    }
                }
                override fun onFailure(
                    call: Call<List<Post>>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.UnitCallBack) {
        PostApi.service.deletePost(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(IOException("HTTP ${response.code()}: ${response.message()}"))
                    }
                }
                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable) {
                    callback.onError(t)
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.UnitCallBack) {
        PostApi.service.likePost(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
//                        callback.onError(IOException("Поставить лайк не удалось("))
                        callback.onError(IOException("SERVER_ERROR"))
                    }
                }
                override fun onFailure(
                    call: Call<Post>,
                    t: Throwable) {
                    callback.onError(t)
                }
            })
    }

    override fun unlikeById(id: Long, callback: PostRepository.UnitCallBack) {
        PostApi.service.unLikePost(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
//                        callback.onError(IOException("Снять лайк не удалось("))
                        callback.onError(IOException("SERVER_ERROR"))
                    }
                }
                override fun onFailure(
                    call: Call<Post>,
                    t: Throwable) {
                    callback.onError(t)
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.GetCallBack<Post>) {
        PostApi.service.savePost(post)
            .enqueue(object : Callback<Post>{

                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { postBody ->
                            callback.onSuccess(postBody)
                        }
                    } else {
//                        callback.onError(RuntimeException(response.errorBody()?.string().orEmpty()))
                        callback.onError(IOException("SERVER_ERROR"))
                    }
                }

                override fun onFailure(
                    call: Call<Post>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }
            })
    }
    override fun send(id: Long) {
        TODO("Not yet implemented")
    }
}