package com.sinau.dicodingstory.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.sinau.dicodingstory.R
import com.sinau.dicodingstory.data.remote.response.Story
import com.sinau.dicodingstory.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extra = intent.extras
        if (extra != null) {
            val id = extra.getString(EXTRA_ID).toString()
            val token = extra.getString(EXTRA_TOKEN).toString()
            getStory(id, token)
        } else {
            Toast.makeText(this@DetailActivity, "Failed to load detail story", Toast.LENGTH_LONG)
                .show()
        }

        supportActionBar?.title = getString(R.string.detail_story)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getStory(id: String, token: String) {
        lifecycleScope.launchWhenResumed {
            launch {
                detailViewModel.getDetailStory(id, token).collect { result ->
                    result.onSuccess {
                        showBinding(it.story)
                    }

                    result.onFailure {
                        Toast.makeText(this@DetailActivity, "Failed to parsing json", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun showBinding(story: Story) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .centerCrop()
                .into(ivStory)
            tvName.text = story.name
            tvDescription.text = story.description
            tvDate.text = story.createdAt
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}