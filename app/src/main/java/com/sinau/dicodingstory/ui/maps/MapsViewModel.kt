package com.sinau.dicodingstory.ui.maps

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.remote.response.StoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class MapsViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getStoriesMaps(token: String): Flow<Result<StoriesResponse>> =
        storyRepository.getStoriesMaps(token)
}