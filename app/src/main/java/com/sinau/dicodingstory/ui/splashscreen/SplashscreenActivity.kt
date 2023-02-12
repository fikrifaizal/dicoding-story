package com.sinau.dicodingstory.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sinau.dicodingstory.databinding.ActivitySplashscreenBinding
import com.sinau.dicodingstory.ui.login.LoginActivity
import com.sinau.dicodingstory.ui.main.MainActivity
import com.sinau.dicodingstory.ui.main.MainActivity.Companion.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    private val splashscreenViewModel: SplashscreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        lifecycleScope.launchWhenCreated {
            launch {
                delay(1000)
                splashscreenViewModel.getToken().collect { token ->
                    if (token.isNullOrEmpty()) {
                        val intent = Intent(this@SplashscreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashscreenActivity, MainActivity::class.java)
                        intent.putExtra(EXTRA_TOKEN, token)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}