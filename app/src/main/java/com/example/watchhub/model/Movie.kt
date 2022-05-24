package com.example.watchhub.model

import com.google.gson.annotations.SerializedName


data class Movie(@SerializedName("results") var results: List<MovieResults>)

data class Cast(@SerializedName("cast") var cast: List<CastResults>)

data class TvShows(@SerializedName("results") var results: List<TvShowResults>)

data class Genres(@SerializedName("genres") var genres: List<GenreResults>)

data class EpisodeDetail(@SerializedName("episodes") var episodes: List<EpisodeDetails>)

data class Trailers(@SerializedName("results") var trailers: List<TrailerResults>)

