package ru.netology.nmedia_practice.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.databinding.FragmentNewPostBinding
import ru.netology.nmedia_practice.utils.AndroidUtils
import ru.netology.nmedia_practice.utils.StringArg
import ru.netology.nmedia_practice.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

              arguments?.textArg.let(binding.edit::setText)

        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
                viewModel.loadPosts()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigate(
                    R.id.feedFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.feedFragment, true)
                        .build()
                )
            }
        }

        return binding.root
    }
}