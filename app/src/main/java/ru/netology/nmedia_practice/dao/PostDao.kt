package ru.netology.nmedia_practice.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia_practice.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>
    @Insert
    fun insert(post: PostEntity)
    @Query("UPDATE PostEntity SET content = :text WHERE id = :id")
    fun updateContentById(id: Long, text: String)

    fun save(post: PostEntity) = if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query("""
        UPDATE PostEntity SET
            countSend = countSend + 1
        WHERE id = :id;
    """)
    fun send(id: Long)

    @Query("""
        UPDATE PostEntity SET
            countLikes = countLikes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
            likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id;
    """)
    fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)
}
