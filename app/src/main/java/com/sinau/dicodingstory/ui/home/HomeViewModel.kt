package com.sinau.dicodingstory.ui.home

import androidx.lifecycle.ViewModel
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getStories(token: String): Flow<Result<StoriesResponse>> = storyRepository.getStories(token)
}