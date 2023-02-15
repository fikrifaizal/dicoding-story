package com.sinau.dicodingstory.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity (

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    val name: String,

    val description: String,

    val lon: Double?,

    val lat: Double?
)