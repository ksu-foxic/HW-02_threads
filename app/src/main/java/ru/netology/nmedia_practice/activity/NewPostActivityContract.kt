package ru.netology.nmedia_practice.activity
import ru.netology.nmedia_practice.dto.Post
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME

class NewPostActivityContract: ActivityResultContract <Post?, Pair<Long, String>?>(){
    override fun createIntent(
        context: Context,
        input: Post?
    ): Intent {
        return Intent(context, NewPostActivity::class.java).apply {
            if (input != null) {
                putExtra("postId", input.id)
                putExtra("postContent", input.content)
            }
        }
    }
    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Pair<Long, String>? {
        if (resultCode != Activity.RESULT_OK || intent == null) return null
        val id = intent.getLongExtra("postId", 0L)
        val content = intent.getStringExtra("postContent") ?: return null
        return id to content
    }
}