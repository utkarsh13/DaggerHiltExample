package com.utkarsh.daggerhiltexample.network

import com.utkarsh.daggerhiltexample.database.DatabaseRepository
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkRepository(
    val author: String,
    val name: String,
    val avatar: String?,
    val url: String,
    val description: String?,
    val language: String?,
    val languageColor: String?,
    val stars: Int?,
    val forks: Int?,
    val currentPeriodStars: Int?
)

fun List<NetworkRepository>.asDBModel(): List<DatabaseRepository> {
    return map {
        DatabaseRepository(
            author = it.author,
            name = it.name,
            avatar = it.avatar,
            url = it.url,
            description = it.description,
            language = it.language,
            languageColor = it.languageColor,
            stars = it.stars,
            forks = it.forks,
            currentPeriodStars = it.currentPeriodStars
        )
    }
}