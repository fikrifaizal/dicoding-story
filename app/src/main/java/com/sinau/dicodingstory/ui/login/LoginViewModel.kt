package com.sinau.dicodingstory.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinau.dicodingstory.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun saveToken(token: String) {
        viewModelScope.launch {
            authRepository.saveToken(token)
        }
    }

    fun getUserLogin(email: String, password: String) =
        authRepository.getUserLogin(email, password)
}