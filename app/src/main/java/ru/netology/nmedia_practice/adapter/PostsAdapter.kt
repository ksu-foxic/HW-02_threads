package ru.netology.nmedia_practice.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.databinding.CardPostBinding
import ru.netology.nmedia_practice.dto.Post

interface PostListener {
    fun onPostClick(post: Post)
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
fun Int.formatCount(): String {
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
            video.setImageResource(R.drawable.video)
            author.text = post.author
            published.text = post.published
            content.text = post.content
            view.text = post.countView.formatCount()
            send.text = post.countSend.formatCount()
            like.isChecked = post.likedByMe
            like.text = post.countLikes.formatCount()

            if (!post.videoUrl.isNullOrBlank()) {
                videoContainer.visibility = View.VISIBLE

                videoContainer.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                    it.context.startActivity(intent)
                }
            } else {
                videoContainer.visibility = View.GONE
            }
            play.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                it.context.startActivity(intent)
            }

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
        binding.root.setOnClickListener {
            listener.onPostClick(post)
        }
        binding.content.setOnClickListener {
            listener.onPostClick(post)
        }
    }
}
object PostDiffUtils : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}