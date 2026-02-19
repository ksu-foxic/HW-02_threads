package ru.netology.nmedia_practice.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.adapter.PostListener
import ru.netology.nmedia_practice.adapter.PostsAdapter
import ru.netology.nmedia_practice.databinding.FragmentFeedBinding
import ru.netology.nmedia_practice.dto.Post
import ru.netology.nmedia_practice.fragment.NewPostFragment.Companion.textArg
import ru.netology.nmedia_practice.viewmodel.PostViewModel
class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(layoutInflater, container, false)

        val viewModel: PostViewModel by viewModels()

        binding.swiperefresh.setOnRefreshListener { viewModel.load() }

        val adapter = PostsAdapter(

            object : PostListener {
                override fun onEdit(post: Post) {

                    viewModel.edit(post)
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    }, 100)
                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        })
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onSend(post: Post) {
                    viewModel.send(post.id)

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(intent, "Share post")
                    startActivity(shareIntent)
                }
                override fun onPostClick(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_editPostFragment, Bundle().apply {
                            putLong("postId", post.id)
                        }
                    )
                }
            })

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.empty.isVisible = state.empty
            binding.swiperefresh.isRefreshing = false
        }

        binding.retry.setOnClickListener {
            viewModel.load()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        return binding.root
    }
}
