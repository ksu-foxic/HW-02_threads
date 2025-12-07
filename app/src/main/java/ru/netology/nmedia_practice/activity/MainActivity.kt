package ru.netology.nmedia_practice.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.adapter.PostListener
import ru.netology.nmedia_practice.adapter.PostsAdapter
import ru.netology.nmedia_practice.databinding.ActivityMainBinding
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.utils.AndroidUtils
import ru.netology.nmedia_practice.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(

            object : PostListener {
                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onSend(post: Post) {
                    viewModel.send(post.id)
                    val intent = Intent()
                        .putExtra(Intent.EXTRA_TEXT, post.content)
                        .setAction(Intent.ACTION_SEND)
                        .setType("text/plain")

                    try {
                        startActivity(Intent.createChooser(intent, null))
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.apps_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        )

        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        binding.list.smoothScrollToPosition(0)
                    }
                }
            }
        )

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {

                binding.editButton1.visibility = View.VISIBLE
                binding.content.setText(post.content)
                AndroidUtils.showKeyboard(binding.content)

            }
        }

        binding.cansel.setOnClickListener {
            viewModel.canselEdit()
            binding.content.setText("")
            binding.editButton1.visibility = View.GONE
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(binding.content)
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                val currentText = text.toString()
                if (currentText.isBlank()) {
                    Toast.makeText(context, R.string.empty_text, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.save(currentText)
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
            binding.editButton1.visibility = View.GONE
        }

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }
}