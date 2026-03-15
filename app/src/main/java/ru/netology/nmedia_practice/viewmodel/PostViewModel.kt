package ru.netology.nmedia_practice.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.model.FeedModel
import ru.netology.nmedia_practice.repository.PostRepository
import ru.netology.nmedia_practice.repository.PostRepositoryImpl
import kotlin.collections.find
import kotlin.collections.map
import kotlin.collections.orEmpty

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = 0,
    likes = 0,
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }
//Дописала
    private fun isServerError(e: Throwable): Boolean {
        return e.message == "SERVER_ERROR" && e !is java.net.SocketTimeoutException
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAll(object : PostRepository.GetCallBack<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.value = FeedModel(
                    posts = result,
                    empty = result.isEmpty(),
                    errorServer = false
                )

            }

            override fun onError(e: Throwable) {

                _data.value = FeedModel(
                    error = true,
                    errorServer = isServerError(e)
                )
            }
        })
    }

    fun removeById(id: Long) {
        val oldPosts = _data.value?.posts.orEmpty()
        _data.postValue( //- отправляет новое значение в LiveData (на UI)
            _data.value?.copy( // - создаёт копию текущего FeedModel с изменениями
                posts = oldPosts.filter { it.id != id }
            )
        )
        repository.removeById(id, object : PostRepository.UnitCallBack {
            override fun onSuccess() {
            }

            override fun onError(e: Throwable) {
                _data.postValue(_data.value?.copy(posts = oldPosts))
                _data.postValue(FeedModel(error = true))
            }
        })
    }
    fun likeById(id: Long) {

        val currentPosts = _data.value?.posts.orEmpty()
        val post = currentPosts.find { it.id == id } ?: return

        val updatePosts = currentPosts.map {
            if (it.id == id) {
                it.copy(
                    likedByMe = !it.likedByMe,
                    likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                )
            } else it
        }
        _data.postValue(_data.value?.copy(posts = updatePosts))

        if (post.likedByMe) {
            repository.unlikeById(id, object : PostRepository.UnitCallBack {
                override fun onSuccess() {
                }

                override fun onError(e: Throwable) {
                    _data.postValue(_data.value?.copy(posts = currentPosts))
                    _data.postValue(FeedModel(
                        error = true,
                        errorServer = isServerError(e)
                    ))
                }
            })
        } else repository.likeById(id, object : PostRepository.UnitCallBack {
            override fun onSuccess() {
            }

            override fun onError(e: Throwable) {
                _data.postValue(_data.value?.copy(posts = currentPosts))
                _data.postValue(FeedModel(
                    error = true,
                    errorServer = isServerError(e)
                ))
            }
        })
    }

    fun send(id: Long) {
        repository.send(id)
    }

    fun save() {
        edited.value?.let {
            repository.save(
                it,
                object : PostRepository.GetCallBack<Post> {
                    override fun onSuccess(result: Post) {
                        _postCreated.postValue(Unit)
                        edited.value = empty
                    }
                    override fun onError(e: Throwable) {
                        _data.postValue(FeedModel(
                            error = true,
                            errorServer = isServerError(e)
                        ))
                    }
                })
        }
//        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun clear() {
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
}
