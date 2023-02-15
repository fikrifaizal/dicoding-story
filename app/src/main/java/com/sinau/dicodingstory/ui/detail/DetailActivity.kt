package com.sinau.dicodingstory.ui.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.Glide
import com.sinau.dicodingstory.R
import com.sinau.dicodingstory.data.remote.response.Story
import com.sinau.dicodingstory.databinding.ActivityDetailBinding
import com.sinau.dicodingstory.utils.animateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID).toString()

        getToken()
        getStory(id)

        supportActionBar?.title = getString(R.string.detail_story)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getToken() {
        lifecycleScope.launchWhenCreated {
            launch {
                detailViewModel.getToken().collect { getToken ->
                    if (!getToken.isNullOrEmpty()) token = getToken
                }
            }
        }
    }

    private fun getStory(id: String) {
        showLoading(true)
        lifecycleScope.launchWhenCreated {
            launch {
                detailViewModel.getDetailStory(id, token).collect { result ->
                    result.onSuccess {
                        showLoading(false)
                        showBinding(it.story)
                    }

                    result.onFailure {
                        showLoading(false)
                        onErrorData()
                        Toast.makeText(
                            this@DetailActivity,
                            "Failed to parsing json",
                            Toast.LENGTH_SHORT
                        )
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

            val img = ObjectAnimator.ofFloat(cvImage, View.ALPHA, 1f).setDuration(250)
            val name = ObjectAnimator.ofFloat(tvName, View.ALPHA, 1f).setDuration(250)
            val desc = ObjectAnimator.ofFloat(tvDescription, View.ALPHA, 1f).setDuration(250)
            val date = ObjectAnimator.ofFloat(tvDate, View.ALPHA, 1f).setDuration(250)

            AnimatorSet().apply {
                playSequentially(img, name, desc, date)
                start()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingLayout.animateLoading(isLoading)
    }

    private fun onErrorData() {
        binding.tvError.animateLoading(true)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}