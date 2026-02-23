package ru.netology.nmedia_practice.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia_practice.databinding.FragmentNewPostBinding
import ru.netology.nmedia_practice.utils.StringArg
import ru.netology.nmedia_practice.viewmodel.PostViewModel
import kotlin.getValue
import ru.netology.nmedia_practice.R
import ru.netology.nmedia_practice.utils.AndroidUtils

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        // Получаем текст из аргументов (если есть - например, из "Поделиться")
        val sharedText = arguments?.textArg

        // Получаем текст из edited (если это редактирование)
        val editText = viewModel.edited.value?.content

        // Устанавливаем приоритет: сначала shared, потом edit, потом пусто
        when {
            !sharedText.isNullOrBlank() -> binding.edit.setText(sharedText)
            !editText.isNullOrBlank() -> binding.edit.setText(editText)
        }

        binding.ok.setOnClickListener {
            if (!binding.edit.text.isNullOrBlank()) {
                viewModel.save(binding.edit.text.toString())
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }


        return binding.root
    }
    companion object {
        var Bundle.textArg: String? by StringArg
    }

}
//        arguments?.textArg?.let(binding.edit::setText)
//
//        binding.ok.setOnClickListener {
//
//            if (!binding.edit.text.isNullOrBlank()) {
//                viewModel.save(binding.edit.text.toString())
//                AndroidUtils.hideKeyboard(requireView())
//                viewModel.loadPosts()
//                findNavController().navigate(
//                    R.id.feedFragment,
//                    null,
//                    NavOptions.Builder()
//                        .setPopUpTo(R.id.feedFragment, true)
//                        .build()
//                )
//            }
//        }
//
//        viewModel.postCreated.observe(viewLifecycleOwner) {
//            viewModel.loadPosts()
//            findNavController().navigateUp()
//        }
//        return binding.root
//    }
//    companion object {
//        var Bundle.textArg: String? by StringArg
//    }
