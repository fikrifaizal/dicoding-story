package com.sinau.dicodingstory.ui.splashscreen

import androidx.lifecycle.ViewModel
import com.sinau.dicodingstory.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SplashscreenViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun getToken(): Flow<String?> = authRepository.getToken()
}