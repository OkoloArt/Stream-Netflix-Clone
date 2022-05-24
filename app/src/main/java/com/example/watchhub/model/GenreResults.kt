package com.example.watchhub.model

import com.google.gson.annotations.SerializedName

data class GenreResults(
    @SerializedName("name") internal val genreName: String,
)