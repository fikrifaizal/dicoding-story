package com.sinau.dicodingstory.utils

import com.sinau.dicodingstory.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyListStory(): List<StoryEntity> {
        val storyItems: MutableList<StoryEntity> = arrayListOf()

        for (i in 0..10) {
            val story = StoryEntity(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Dimas",
                description = "Lorem Ipsum",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                lat = -10.212,
                lon = -16.002
            )
            storyItems.add(story)
        }

        return storyItems
    }
}