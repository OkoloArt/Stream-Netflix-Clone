package com.example.watchhub.model

import com.google.gson.annotations.SerializedName

data class TvShowResults(

    @SerializedName("name") internal val tvTitle: String,

    @SerializedName("id")
    internal val tvId: Int,

    @SerializedName("number_of_seasons") internal val tvNumberOfSeasons: Int,

    @SerializedName("first_air_date")
    internal val firstAirDate: String,

    @SerializedName("tagline")
    internal val tagLine: String,

    @SerializedName("overview")
    internal val tvOverview: String,

    @SerializedName("vote_average")
    internal val tvShowVoteAverage: Double,

    @SerializedName("homepage") internal val homepage: String,

    @SerializedName("poster_path") internal val tvPosterPath: String,
)