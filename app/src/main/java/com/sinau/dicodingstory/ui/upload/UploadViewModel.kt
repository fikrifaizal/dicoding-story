package com.sinau.dicodingstory.ui.upload

import androidx.lifecycle.ViewModel
import com.sinau.dicodingstory.data.AuthRepository
import com.sinau.dicodingstory.data.StoryRepository
import com.sinau.dicodingstory.data.remote.response.UploadResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getToken(): Flow<String?> = authRepository.getToken()

    fun uploadStory(
        token: String,
        description: String,
        file: MultipartBody.Part
    ): Flow<Result<UploadResponse>> = storyRepository.uploadStory(token, description, file)
}