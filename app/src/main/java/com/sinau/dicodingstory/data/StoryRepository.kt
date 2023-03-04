package com.sinau.dicodingstory.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sinau.dicodingstory.data.local.entity.StoryEntity
import com.sinau.dicodingstory.data.local.room.StoryDatabase
import com.sinau.dicodingstory.data.remote.StoryRemoteMediator
import com.sinau.dicodingstory.data.remote.api.ApiService
import com.sinau.dicodingstory.data.remote.response.DetailStoryResponse
import com.sinau.dicodingstory.data.remote.response.StoriesResponse
import com.sinau.dicodingstory.data.remote.response.UploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    fun getStories(token: String): Flow<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                apiService,
                generateBearerToken(token)
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow
    }

    fun getStoriesMaps(token: String): Flow<Result<StoriesResponse>> = flow {
        try {
            val response = apiService.getAllStories(generateBearerToken(token), location = 1)
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

    fun uploadStory(
        token: String,
        description: RequestBody,
        file: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<UploadResponse>> = flow {
        try {
            val response =
                apiService.upLoadStory(generateBearerToken(token), description, file, lat, lon)
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