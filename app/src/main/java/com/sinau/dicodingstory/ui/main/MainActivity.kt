package com.sinau.dicodingstory.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinau.dicodingstory.data.remote.response.ListStoryItem
import com.sinau.dicodingstory.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(EXTRA_TOKEN).toString()

        getStory()
    }

    private fun getStory() {
        lifecycleScope.launchWhenResumed {
            launch {
                mainViewModel.getStories(token).collect { result ->
                    result.onSuccess {
                        showRecyclerView(it.listStory)
                    }

                    result.onFailure {
                        Toast.makeText(this@MainActivity, "Failed to parsing json", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun showRecyclerView(listStory: List<ListStoryItem>) {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = StoryAdapter(listStory, token)
            setHasFixedSize(true)
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}