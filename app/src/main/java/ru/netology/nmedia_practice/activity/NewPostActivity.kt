package ru.netology.nmedia_practice.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Добавляли
        val binding = ActivityNewPostBinding.inflate(layoutInflater)

        setContentView(binding.root)//Закреплям контент

        val postId = intent.getLongExtra("postId", 0L)
        val postContent = intent.getStringExtra("postContent")

        postContent?.let {
            binding.content.setText(it)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->//в скобках меняем на bindig.root
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.save.setOnClickListener {
            val text = binding.content.text.toString().trim()

            if(text.isBlank()) {
                setResult(RESULT_CANCELED)
            } else {
                val result = Intent().apply {
                    putExtra("postId", postId)
                    putExtra("postContent", text)
                }
                setResult(RESULT_OK, result)
            }
            finish()
        }
    }
}