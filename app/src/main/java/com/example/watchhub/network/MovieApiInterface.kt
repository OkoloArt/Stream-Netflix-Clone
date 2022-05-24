package com.example.watchhub.network

import com.example.watchhub.model.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MovieApiInterface {


    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<Movie?>?

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String?,
    @Query("language") language: String?,
    ): Call<MovieResults?>?

    @GET("movie/{movie_id}")
    fun getMovieGenre(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
    ): Call<Genres?>?

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<Movie?>?

    @GET("movie/now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<Movie?>?

    @GET("movie/{movie_id}/credits")
    fun getCast(
        @Path("movie_id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?
    ): Call<Cast?>?

    @GET("movie/{movie_id}/videos")
    fun getMovieTrailer(
        @Path("movie_id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?
    ): Call<Trailers?>?

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("region") region: String,
        @Query("with_release_type") release_type: String
    ): Call<Movie?>?

    @GET("movie/{movie_id}/recommendations")
    fun getRecommendations(
        @Path("movie_id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int?
    ): Call<Movie?>?

    @GET("tv/popular")
    fun getPopularTvShows(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<TvShows?>?

    @GET("tv/on_the_air")
    fun getTvShowsOnAir(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<TvShows?>?

    @GET("tv/top_rated")
    fun getTopRatedTvShows(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<TvShows?>?

    @GET("tv/airing_today")
    fun getTvShowsAiringToday(
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<TvShows?>?

    @GET("tv/{tv_id}")
    fun getTvShowDetail(
        @Path("tv_id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
    ): Call<TvShowResults?>?

    @GET("tv/{tv_id}/season/{season_number}")
    fun getEpisodeDetail(
        @Path("tv_id") id: Int?,
        @Path("season_number") seasonNumber: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
    ): Call<EpisodeDetail?>?

    @GET("tv/{tv_id}/similar")
    fun getSimilarTvShows(
        @Path("tv_id") id: Int?,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
        @Query("page") pages: Int
    ): Call<TvShows?>?

    @GET("tv/{tv_id}")
    fun getTvShowGenre(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
    ): Call<Genres?>?

    @GET("tv/{tv_id}/videos")
    fun getTvShowTrailers(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String?,
        @Query("language") language: String?,
    ): Call<Trailers?>?
}