package com.sinau.dicodingstory.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sinau.dicodingstory.databinding.ActivityRegisterBinding
import com.sinau.dicodingstory.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnRegister.setOnClickListener {
                registerHandler()
            }
            btnTextLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        supportActionBar?.hide()
    }

    private fun registerHandler() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        lifecycleScope.launchWhenResumed {
            launch {
                registerViewModel.saveUserRegister(name, email, password).collect { result ->
                    result.onSuccess {
                        Toast.makeText(this@RegisterActivity, "Register success", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    result.onFailure {
                        Toast.makeText(this@RegisterActivity, "Register failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}