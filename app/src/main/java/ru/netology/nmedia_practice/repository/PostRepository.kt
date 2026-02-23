package ru.netology.nmedia_practice.repository
import androidx.lifecycle.LiveData
import ru.netology.nmedia_practice.dto.Post
interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun unlikeById(id: Long)
    fun send(id: Long)
    fun removeById(id: Long)
    fun save(post: Post): Post
    fun getAllAsync(callback: GetCallBack<List<Post>>)
    fun removeByIdAsync (id: Long, callback: UnitCallBack)
    fun likeByIdAsync(id: Long, callback: UnitCallBack)
    fun unlikeByIdAsync(id: Long, callback: UnitCallBack)
    fun saveAsync(post: Post, callback: GetCallBack<Post>)

    interface GetCallBack<T> {
        fun onSuccess (result: T)
        fun onError(e: Exception)
    }
   interface UnitCallBack {
       fun onSuccess ()
       fun onError(e: Exception)
   }
}

//    interface GetAllCallBack {
//        fun onSuccess(posts: List<Post>)
//        fun onError(e: Exception)