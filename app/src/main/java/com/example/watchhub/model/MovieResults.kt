package com.example.watchhub.model

import com.google.gson.annotations.SerializedName


data class MovieResults(

    @SerializedName("title") internal val movieTitle: String,

    @SerializedName("overview")
    internal val movieOverview: String,

    @SerializedName("vote_average")
    internal val movieVoteAverage: Double,

    @SerializedName("id")
    internal val movieId: Int,

    @SerializedName("poster_path") internal val moviePosterPath: String,

    @SerializedName("release_date")
    internal val movieReleaseDate: String,

    @SerializedName("homepage")
    internal val movieHomePage: String,

    @SerializedName("runtime")
    internal val movieRuntime: Int,

    @SerializedName("tagline")
    internal val movieTagLine: String,
)
