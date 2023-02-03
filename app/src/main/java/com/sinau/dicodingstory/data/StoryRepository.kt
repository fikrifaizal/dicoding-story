package com.sinau.dicodingstory.data

import com.sinau.dicodingstory.data.remote.api.ApiService
import com.sinau.dicodingstory.data.remote.response.DetailStoryResponse
import com.sinau.dicodingstory.data.remote.response.StoriesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoryRepository @Inject constructor(private val apiService: ApiService) {

    fun getStories(token: String): Flow<Result<StoriesResponse>> = flow {
        try {
            val response = apiService.getAllStories(generateBearerToken(token))
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    fun getDetailStory(id: String, token: String): Flow<Result<DetailStoryResponse>> = flow {
        try {
            val response = apiService.getDetailStory(id, generateBearerToken(token))
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun generateBearerToken(token: String): String {
        return if (token.contains("bearer", ignoreCase = true)) {
            token
        } else {
            "Bearer $token"
        }
    }
}