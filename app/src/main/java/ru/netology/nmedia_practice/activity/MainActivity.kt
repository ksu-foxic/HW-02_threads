package ru.netology.nmedia_practice.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.adapter.PostsAdapter
import ru.netology.nmedia_practice.databinding.ActivityMainBinding
import ru.netology.nmedia_practice.databinding.CardPostBinding
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
            onLikeListener = { viewModel.likeById(it.id) },
            onSendListener = { viewModel.send(it.id) }
        )

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }
}