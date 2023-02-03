package com.sinau.dicodingstory.ui.register

import androidx.lifecycle.ViewModel
import com.sinau.dicodingstory.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun saveUserRegister(name: String, email: String, password: String) = authRepository.saveUserRegister(name, email, password)
}