package com.example.watchhub.model

import com.google.gson.annotations.SerializedName

data class CastResults(
    @SerializedName("name")
    internal val castName: String,
    @SerializedName("profile_path")
    internal val castProfilePath: String,
)