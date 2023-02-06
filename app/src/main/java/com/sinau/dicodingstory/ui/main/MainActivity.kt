package com.sinau.dicodingstory.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sinau.dicodingstory.R
import com.sinau.dicodingstory.data.remote.response.ListStoryItem
import com.sinau.dicodingstory.databinding.ActivityMainBinding
import com.sinau.dicodingstory.ui.login.LoginActivity
import com.sinau.dicodingstory.ui.upload.UploadActivity
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
            R.id.menu_setting -> {
                Toast.makeText(this@MainActivity, "Coming Soon", Toast.LENGTH_SHORT)
                    .show()
                true
            }
            R.id.menu_logout -> {
                mainViewModel.clearToken()
                Toast.makeText(this@MainActivity, "Logout Success", Toast.LENGTH_LONG)
                    .show()
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
        lifecycleScope.launchWhenResumed {
            launch {
                mainViewModel.getStories(token).collect { result ->
                    result.onSuccess {
                        binding.loadingLayout.visibility = View.GONE
                        showRecyclerView(it.listStory)
                    }

                    result.onFailure {
                        binding.loadingLayout.visibility = View.GONE
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
            adapter = StoryAdapter(listStory)
            setHasFixedSize(true)
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}