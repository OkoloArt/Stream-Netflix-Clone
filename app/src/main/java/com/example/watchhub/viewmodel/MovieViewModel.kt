package com.example.watchhub.viewmodel

import androidx.lifecycle.*


class MovieViewModel() : ViewModel() {

    private lateinit var _movieName: String
    val movieName get() = _movieName

    private lateinit var _movieImage: String
    val movieImage get() = _movieImage

    private var _movieRating: Double = 0.0
    val movieRating get() = _movieRating

    private lateinit var _movieOverview: String
    val movieOverview get() = _movieOverview

    private var _movieId: Int = 0
    val movieId get() = _movieId

    private var _episodeNumber: Int = 0
    val episodeNumber get() = _episodeNumber


    fun setEpisodeNumber(number: Int) {
        _episodeNumber = number
    }

    fun setMovieId(id: Int) {
        _movieId = id
    }

    fun setMovieName(name: String) {
        _movieName = name
    }

    fun setMovieRating(rating: Double) {
        _movieRating = rating
    }

    fun setMovieOverview(name: String) {
        _movieOverview = name
    }

    fun setMovieImage(movieImage: String) {
        _movieImage = movieImage
    }
}
