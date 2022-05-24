package com.example.watchhub.model

import com.google.gson.annotations.SerializedName

data class EpisodeDetails(

    @SerializedName("name") internal val episodeName: String,

    @SerializedName("overview")
    internal val episodeOverview: String,

    @SerializedName("episode_number")
    internal val episodeNumber: String,

    @SerializedName("season_number")
    internal val episodeSeasonNumber: String,

    @SerializedName("runtime")
    internal val episodeRuntime: String,

    @SerializedName("id")
    internal val episodeId: Int,

    @SerializedName("still_path") internal val episodePosterPath: String,

    )