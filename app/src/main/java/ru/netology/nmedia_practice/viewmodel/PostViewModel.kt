package ru.netology.nmedia_practice.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.util.SingleLiveEvent
import ru.netology.nmedia_practice.db.AppDb
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.model.FeedModel
import ru.netology.nmedia_practice.repository.PostRepository
import ru.netology.nmedia_practice.repository.PostRepositoryImpl
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    countLikes = 0,
    likedByMe = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)

    private val _error = SingleLiveEvent<String>()

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    //крутит перед загрузкой приложения
    init {
        load()
    }

    fun load() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))

            val result = try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (_: Exception) {
                // Получена ошибка
                FeedModel(error = true)
            }
            _data.postValue(result)
        }
    }

    fun likeById(id: Long) {
        thread {
            val currentPosts = _data.value?.posts.orEmpty()
            val post = currentPosts.find { it.id == id } ?: return@thread

            val updatePosts = currentPosts.map {
                if (it.id == id) {
                    it.copy(
                        likedByMe = !it.likedByMe,
                        countLikes = if (it.likedByMe) it.countLikes - 1 else it.countLikes + 1
                    )
                } else it
            }
            _data.postValue(_data.value?.copy(posts = updatePosts))

            try {
                if (post.likedByMe) {
                    repository.unlikeById(id)
                } else {
                    repository.unlikeById(id)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // В случае ошибки - возвращаем как было
                _data.postValue(_data.value?.copy(posts = currentPosts))
                _error.postValue("Не удалось поставить лайк. Проверьте соединение.")
            }
        }
    }

    fun send(id: Long) {
        repository.send(id)
    }

    fun removeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue( //- отправляет новое значение в LiveData (на UI)
                _data.value?.copy( // - создаёт копию текущего FeedModel с изменениями
                    posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id } //- новый список постов:
                )
            )
            try {
                repository.removeById(id) // Пытаемся удалить пост на сервере.
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old)) // отправляет новое значение восстановленного старого списка постов
            }
        }
    }

    fun save(content: String) {

        val postToSave = edited.value

        thread {
            edited.value?.let {

                val text = content.trim()
                if (it.content != text) {
                    repository.save(it.copy(content = text))
                    _postCreated.postValue(Unit)
                }
            }
            edited.postValue(empty)
        }

    }
    fun edit(post: Post) {
        edited.value = post
    }
}