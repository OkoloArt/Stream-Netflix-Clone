package com.example.watchhub.model

import com.google.gson.annotations.SerializedName

data class TrailerResults(
    @SerializedName("name") internal val trailerName: String,

    @SerializedName("key")
    internal val trailerKey: String,
)