package ru.netology.nmedia_practice.repository

import ru.netology.nmedia_practice.dto.Post
interface PostRepository {
    fun send(id: Long)
    fun getAll(callback: GetCallBack<List<Post>>)
    fun removeById (id: Long, callback: UnitCallBack)
    fun likeById(id: Long, callback: UnitCallBack)
    fun unlikeById(id: Long, callback: UnitCallBack)
    fun save(post: Post, callback: GetCallBack<Post>)

    interface GetCallBack<T> {
        fun onSuccess (result: T)
        fun onError(e: Throwable)
    }
   interface UnitCallBack {
       fun onSuccess ()
       fun onError(e: Throwable)
   }
}