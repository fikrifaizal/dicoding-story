package com.sinau.dicodingstory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.local.entity.StoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class HomeViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getStories(token: String): LiveData<PagingData<StoryEntity>> =
        storyRepository.getStories(token).cachedIn(viewModelScope).asLiveData()
}