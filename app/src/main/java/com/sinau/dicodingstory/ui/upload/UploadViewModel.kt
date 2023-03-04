package com.sinau.dicodingstory.ui.upload

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.sinau.dicodingstory.data.AuthRepository
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.remote.response.UploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
@ExperimentalPagingApi
class UploadViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getToken(): Flow<String?> = authRepository.getToken()

    fun uploadStory(
        token: String,
        description: RequestBody,
        file: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<UploadResponse>> =
        storyRepository.uploadStory(token, description, file, lat, lon)
}