package com.sinau.dicodingstory.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sinau.dicodingstory.databinding.ActivityLoginBinding
import com.sinau.dicodingstory.ui.main.MainActivity
import com.sinau.dicodingstory.ui.main.MainActivity.Companion.EXTRA_TOKEN
import com.sinau.dicodingstory.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLogin.setOnClickListener {
                loginHandler()
            }
            btnTextRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        supportActionBar?.hide()
    }

    private fun loginHandler() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        lifecycleScope.launchWhenResumed {
            launch {
                loginViewModel.getUserLogin(email, password).collect { result ->
                    result.onSuccess {
                        it.loginResult.token.let { token ->
                            loginViewModel.saveToken(token)
                            Toast.makeText(this@LoginActivity, "Login success", Toast.LENGTH_SHORT)
                                .show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra(EXTRA_TOKEN, token)
                            startActivity(intent)
                            finish()
                        }
                    }

                    result.onFailure {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}