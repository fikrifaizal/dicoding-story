package com.sinau.dicodingstory.data.remote.api

import com.sinau.dicodingstory.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // [require] suspend to enable creating call adapter for response class
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): StoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun upLoadStory(
        @Header("Authorization") token: String,
        @Part("description") description: String,
        @Part file: MultipartBody.Part
    ): UploadResponse
}