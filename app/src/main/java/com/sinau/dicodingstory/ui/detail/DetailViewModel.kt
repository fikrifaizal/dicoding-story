package com.sinau.dicodingstory.ui.detail

import androidx.lifecycle.ViewModel
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.remote.response.DetailStoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val storyRepository: StoryRepository): ViewModel() {

    fun getDetailStory(id: String, token: String): Flow<Result<DetailStoryResponse>> = storyRepository.getDetailStory(id, token)
}