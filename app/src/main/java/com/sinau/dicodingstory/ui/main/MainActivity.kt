package com.sinau.dicodingstory.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinau.dicodingstory.R
import com.sinau.dicodingstory.data.remote.response.ListStoryItem
import com.sinau.dicodingstory.databinding.ActivityMainBinding
import com.sinau.dicodingstory.ui.login.LoginActivity
import com.sinau.dicodingstory.ui.upload.UploadActivity
import com.sinau.dicodingstory.utils.animateLoading
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = intent.getStringExtra(EXTRA_TOKEN).toString()

        getStories(token)

        binding.fabUpload.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                showLoading(true)
                mainViewModel.clearToken()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun getStories(token: String) {
        showLoading(true)
        onErrorData(false)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.getStories(token).collect { result ->
                    result.onSuccess {
                        showLoading(false)
                        showRecyclerView()
                        updateRecyclerView(it.listStory)
                    }

                    result.onFailure {
                        showLoading(false)
                        onErrorData(true)
                    }
                }
            }
        }
    }

    private fun showRecyclerView() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    private fun updateRecyclerView(listStory: List<ListStoryItem>) {
        val firstState = binding.rvStories.layoutManager?.onSaveInstanceState()
        binding.rvStories.adapter = StoryAdapter(listStory)
        binding.rvStories.layoutManager?.onRestoreInstanceState(firstState)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            loadingLayout.animateLoading(isLoading)
            fabUpload.isEnabled = !isLoading
        }
    }

    private fun onErrorData(isError: Boolean) {
        binding.apply {
            rvStories.alpha = if (isError) 0F else 1F
            tvError.animateLoading(isError)
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}