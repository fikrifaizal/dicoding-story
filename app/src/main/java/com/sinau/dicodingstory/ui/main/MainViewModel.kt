package com.sinau.dicodingstory.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinau.dicodingstory.data.AuthRepository
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val authRepository: AuthRepository, private val storyRepository: StoryRepository): ViewModel() {

    fun clearToken() {
        viewModelScope.launch {
            authRepository.clearToken()
        }
    }

    fun getStories(token: String): Flow<Result<StoriesResponse>> = storyRepository.getStories(token)
}