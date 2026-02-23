package ru.netology.nmedia_practice.viewmodel

import android.app.Application
import android.util.Log
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
    private val _error = SingleLiveEvent<String>()
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.GetCallBack<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
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
        repository.removeByIdAsync(id, object : PostRepository.UnitCallBack {
            override fun onSuccess() {
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = oldPosts))
                _error.postValue("Не удалось удалить пост: ${e.message}")
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
            repository.unlikeByIdAsync(id, object : PostRepository.UnitCallBack {
                override fun onSuccess() {
                }
                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(posts = currentPosts))
                    _error.postValue("Не удалось снять лайк. Проверьте соединение.")
                }

            })
        } else repository.likeByIdAsync(id, object : PostRepository.UnitCallBack {
            override fun onSuccess() {
            }
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = currentPosts))
                _error.postValue("Не удалось поставить лайк. Проверьте соединение.")
            }
        })
    }

    fun send(id: Long) {
        repository.send(id)
    }

    fun save(content: String) {
        val post = edited.value ?: empty  // хотя edited.value никогда не null, но для безопасности

        val text = content.trim().takeUnless { it.isEmpty() }
            ?: return _error.postValue("Пустой текст не сохраняется")

        val postToSave = post.copy(content = text)

        repository.saveAsync(postToSave, object : PostRepository.GetCallBack<Post> {
            override fun onSuccess(result: Post) {
                Log.d("PostViewModel", "Post saved with id: ${result.id}")
                val currentPosts = _data.value?.posts.orEmpty()
                val updatedPosts = if (post.id == 0L) {
                    // Новый пост - добавляем в начало
                    listOf(result) + currentPosts
                } else {
                    // Редактирование - заменяем существующий
                    currentPosts.map { if (it.id == result.id) result else it }
                }

                _data.postValue(_data.value?.copy(posts = updatedPosts))
                _postCreated.postValue(Unit)
                edited.postValue(empty)  // Сбрасываем на empty
            }

            override fun onError(e: Exception) {
                Log.e("PostViewModel", "Save error", e)
                _error.postValue("Ошибка сохранения: ${e.message}")
            }
        })
    }

    fun edit(post: Post) {
        edited.value = post
    }
}


//    fun save(content: String) {
//
//        thread {
//
//            edited.value?.let {
//                val text = content.trim()
//                if (it.content != text) {
//                    repository.save(it.copy(content = text))
//                    _postCreated.postValue(Unit)
//                }
//            }
//            edited.postValue(empty)
//        }
//    }
//edited.value?.let {
//                val text = content.trim()
//                if (it.content != text) {
//                    repository.save(it.copy(content = text))
//                    _postCreated.postValue(Unit)
//                }
//            }
//            edited.postValue(empty)


//    fun load() {
//        thread {
//            _data.postValue(FeedModel(loading = true)) // Начинаем загрузку
//            val result = try {
//                val posts = repository.getAll() // Данные успешно получены
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (_: Exception) {
//                FeedModel(error = true) // Получена ошибка
//            }
//            _data.postValue(result)
//        }
//    }

//fun likeById(id: Long) {
//    thread {
//        val currentPosts = _data.value?.posts.orEmpty()
//        val post = currentPosts.find { it.id == id } ?: return@thread
//
//        val updatePosts = currentPosts.map {
//            if (it.id == id) {
//                it.copy(
//                    likedByMe = !it.likedByMe,
//                    countLikes = if (it.likedByMe) it.countLikes - 1 else it.countLikes + 1
//                )
//            } else it
//        }
//        _data.postValue(_data.value?.copy(posts = updatePosts))
//
//        try {
//            if (post.likedByMe) {
//                repository.unlikeById(id)
//            } else {
//                repository.unlikeById(id)
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            // В случае ошибки - возвращаем как было
//            _data.postValue(_data.value?.copy(posts = currentPosts))
//            _error.postValue("Не удалось поставить лайк. Проверьте соединение.")
//        }
//    }
//}

//    fun removeById(id: Long) {
//        thread {
//            val old = _data.value?.posts.orEmpty()
//            _data.postValue( //- отправляет новое значение в LiveData (на UI)
//                _data.value?.copy( // - создаёт копию текущего FeedModel с изменениями
//                    posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id } //- новый список постов:
//                )
//            )
//            try {
//                repository.removeById(id) // Пытаемся удалить пост на сервере.
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old)) // отправляет новое значение восстановленного старого списка постов
//            }
//        }
//    }