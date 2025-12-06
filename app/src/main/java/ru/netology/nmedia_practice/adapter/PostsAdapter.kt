package ru.netology.nmedia_practice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.databinding.CardPostBinding
import ru.netology.nmedia_practice.dto.Post

interface PostListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onSend(post: Post)
}
class PostsAdapter(
    private val listener: PostListener
) : ListAdapter<Post, PostViewHolder>(
    PostDiffUtils
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
fun Long.formatCount(): String {
    return when {
        this < 1_000 -> "$this"
        this < 10_000 -> "${this / 1000}.${(this % 1000) / 100}K"
        this < 1_000_000 -> "${this / 1_000}K"
        else -> "${this / 1_000_000}.${(this % 1_000_000) / 100_000}M"
    }
}
class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            avatar.setImageResource(R.drawable.netology)
            author.text = post.author
            published.text = post.published
            content.text = post.content
            countLikes.text = post.countLikes.formatCount()
            countView.text = post.countView.formatCount()
            countSend.text = post.countSend.formatCount()

            like.setImageResource(
                if (post.likedByMe) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_24
            )
            like.setOnClickListener {
                listener.onLike(post)
            }
            send.setOnClickListener {
                listener.onSend(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}
object PostDiffUtils : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}