package com.example.mymovies.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String = "",
    val title: String = "",
    val directors: List<String> = emptyList(),
    val actors: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val year: Int = 0,
    val length: Int = 0, // in minutes
    val studio: String = "",
    val description: String = ""
) : Parcelable 