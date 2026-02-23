package ru.netology.nmedia_practice.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia_practice.databinding.FragmentPostBinding
import ru.netology.nmedia_practice.adapter.PostsAdapter
import android.content.Intent
import ru.netology.nmedia_practice.viewmodel.PostViewModel
import ru.netology.nmedia_practice.adapter.PostListener
import ru.netology.nmedia_practice.dto.Post
import kotlin.getValue
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.fragment.NewPostFragment.Companion.textArg

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        val postId = requireArguments().getLong("postId")

        val adapter = PostsAdapter(
            object : PostListener {

                override fun onPostClick(post: Post) {
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
//                    findNavController().navigate(R.id.action_editPostFragment_to_newPostFragment
//                    )
                    //добавила 4 строки
                    findNavController().navigate(R.id.action_editPostFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
//                            putLong("postId", post.id)
                        })

                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                    findNavController().navigateUp()
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onSend(post: Post) {
                    viewModel.send(post.id)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(intent, "Share post"))
                }
            }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {feedmodel ->
            adapter.submitList(feedmodel.posts.filter{it.id == postId})
        }

        return binding.root
    }
}