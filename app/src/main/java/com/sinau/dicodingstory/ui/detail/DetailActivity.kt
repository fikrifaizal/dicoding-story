package com.sinau.dicodingstory.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.sinau.dicodingstory.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}