package com.example.watchhub.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchhub.viewmodel.MovieViewModel
import com.example.watchhub.R
import com.example.watchhub.adapter.MovieRecyclerAdapter
import com.example.watchhub.database.MovieApplication
import com.example.watchhub.database.MovieDatabaseViewModel
import com.example.watchhub.database.MovieDatabaseViewModelFactory
import com.example.watchhub.databinding.FragmentActionBinding
import com.example.watchhub.fragment.MovieBottomSheetFragment.Companion.TAG
import com.example.watchhub.model.Movie
import com.example.watchhub.model.MovieResults
import com.example.watchhub.network.MovieApiInterface
import com.example.watchhub.network.RetrofitInstance.retrofitInstance
import com.example.watchhub.utils.ConnectivityLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.random.Random


class ActionFragment : Fragment() {

    val movieViewModel: MovieViewModel by activityViewModels()
    private val movieDatabaseViewModel: MovieDatabaseViewModel by activityViewModels {
        MovieDatabaseViewModelFactory((activity?.application as MovieApplication).database.testDao())
    }

    private var _binding: FragmentActionBinding? = null
    private val binding get() = _binding!!

    var popularMovieResultsList = mutableListOf<MovieResults>(
    )
    var singleMovieResultsList = mutableListOf<MovieResults>(
    )
    var topRatedMovieResultsList = mutableListOf<MovieResults>(
    )

    private lateinit var adapter: MovieRecyclerAdapter

    private var movieName = ""
    private var movieId = 0
    private var movieImage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityLiveData = ConnectivityLiveData(activity!!.application)
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    retrieveNowPlayingMovieJson(API_KEY, LANGUAGE, PAGES)
                    retrievePopularMovieJson(API_KEY, LANGUAGE, PAGES)
                    retrieveTopRatedMovieJson(API_KEY, LANGUAGE, PAGES)
                    Timer().scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            retrieveNowPlayingMovieJson(API_KEY, LANGUAGE, PAGES)
                        }

                    }, 0, 25000)
                }
                false -> {
                    if (findNavController().currentDestination?.id == R.id.homeFragment) {
                        findNavController().navigate(R.id.action_homeFragment_self)
                    }
                }
            }

        })

        binding.myList.setOnClickListener {
           addFavouriteMovies()
        }
        binding.removeList.setOnClickListener {
            deleteFromFavourite()
        }
    }

    private fun retrievePopularMovieJson(apiKey: String?, language: String?, pages: Int) {
        val movieCall: Call<Movie?>? = retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getPopularMovies(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<Movie?> {
            override fun onResponse(call: Call<Movie?>?, response: Response<Movie?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    binding.popularProgressBar.visibility = View.INVISIBLE
                    popularMovieResultsList.clear()
                    popularMovieResultsList = response.body()?.results as MutableList<MovieResults>

                    adapter = MovieRecyclerAdapter(popularMovieResultsList) {
                        movieViewModel.setMovieName(it.movieTitle)
                        movieViewModel.setMovieId(it.movieId)
                        movieViewModel.setMovieImage(it.moviePosterPath)
                        movieViewModel.setMovieOverview(it.movieOverview)
                        movieViewModel.setMovieRating(it.movieVoteAverage)
                        showBottomDialog()
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    binding.recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Movie?>?, t: Throwable) {
            }
        })
    }

    private fun retrieveNowPlayingMovieJson(apiKey: String, language: String, pages: Int) {
        val movieCall: Call<Movie?>? =
            retrofitInstance!!.create(MovieApiInterface::class.java)
                .getNowPlayingMovies(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<Movie?> {
            override fun onResponse(call: Call<Movie?>, response: Response<Movie?>) {
                if (response.isSuccessful && response.body() != null) {

                    singleMovieResultsList.clear()
                    singleMovieResultsList = response.body()?.results as MutableList<MovieResults>
                    val webUrl = "https://imdbembed.xyz/movie/tmdb/"
                    checkUser()

                    val randomIndex = Random.nextInt(singleMovieResultsList.size)
         //           binding.singleMovieTitle.text = singleMovieResultsList[randomIndex].movieTitle
                    val image =
                        "https://image.tmdb.org/t/p/w342${singleMovieResultsList[randomIndex].moviePosterPath}"
                    Picasso.get().load(image).into(binding.singleMovieImage)
                    movieName = singleMovieResultsList[randomIndex].movieTitle
                    movieImage = singleMovieResultsList[randomIndex].moviePosterPath
                    movieId = singleMovieResultsList[randomIndex].movieId
                    checkUser()

                    binding.moreInfo.setOnClickListener {
                        movieViewModel.setMovieName(singleMovieResultsList[randomIndex].movieTitle)
                        movieViewModel.setMovieImage(singleMovieResultsList[randomIndex].moviePosterPath)
                        movieViewModel.setMovieOverview(singleMovieResultsList[randomIndex].movieOverview)
                        movieViewModel.setMovieId(singleMovieResultsList[randomIndex].movieId)
                        showBottomDialog()
                    }
                    val id = singleMovieResultsList[randomIndex].movieId
                    binding.play.setOnClickListener {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.title))
                            .setMessage(resources.getString(R.string.supporting_text))
                            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                                // Respond to negative button press
                                dialog.cancel()
                            }
                            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                                // Respond to positive button press
                                openWebPage("$webUrl$id")
                            }
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<Movie?>, t: Throwable) {
            }

        })
    }

    private fun retrieveTopRatedMovieJson(apiKey: String?, language: String?, pages: Int) {
        val movieCall: Call<Movie?>? = retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getTopRatedMovies(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<Movie?> {
            override fun onResponse(call: Call<Movie?>?, response: Response<Movie?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    binding.topRatedProgressBar.visibility = View.INVISIBLE
                    topRatedMovieResultsList.clear()
                    topRatedMovieResultsList = response.body()?.results as MutableList<MovieResults>

                    adapter = MovieRecyclerAdapter(topRatedMovieResultsList) {
                        movieViewModel.setMovieName(it.movieTitle)
                        movieViewModel.setMovieId(it.movieId)
                        movieViewModel.setMovieImage(it.moviePosterPath)
                        movieViewModel.setMovieOverview(it.movieOverview)
                        movieViewModel.setMovieRating(it.movieVoteAverage)
                        showBottomDialog()
                    }
                    binding.topRatedRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    binding.topRatedRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Movie?>?, t: Throwable) {
            }
        })
    }

    private fun addFavouriteMovies() {
        runBlocking {
            launch {
                val user = movieDatabaseViewModel.checkUser(movieName, movieId)
                if (isEntryValid()) {
                    if (user != null) {
                        Toast.makeText(
                            activity,
                            "Movie Already Added To Favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        movieDatabaseViewModel.addNewItem(
                            movieName,
                            movieImage,
                            movieId
                        )
                        Toast.makeText(activity, "Successfully Added", Toast.LENGTH_SHORT).show()
                        binding.myList.visibility = View.INVISIBLE
                        binding.removeList.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun deleteFromFavourite() {
        runBlocking {
            launch {
                val user = movieDatabaseViewModel.checkUser(movieName, movieId)
                if (isEntryValid()) {
                    if (user != null) {
                        movieDatabaseViewModel.deleteItem(movieName)
                        Toast.makeText(
                            activity,
                            "Successfully Deleted From Database",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.myList.visibility = View.VISIBLE
                        binding.removeList.visibility = View.INVISIBLE

                    }
                }
            }
        }

    }

    private fun checkUser() {
        runBlocking {
            launch {
                val user = movieDatabaseViewModel.checkUser(movieName, movieId)
                if (isEntryValid()) {
                    if (user != null) {
                        binding.myList.visibility = View.INVISIBLE
                        binding.removeList.visibility = View.VISIBLE
                    } else {
                        binding.myList.visibility = View.VISIBLE
                        binding.removeList.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return movieDatabaseViewModel.isEntryValid(
            movieImage, movieId
        )
    }

    companion object {
        private const val LANGUAGE = "en-US"
        private const val API_KEY = "7af2394d40b06bda9cd7d96cb7a29d3f"
        private const val PAGES = 1
    }

    /**
     * If the fragment is already visible, don't show it again. Otherwise, show it
     *
     * @return A boolean value
     */
    fun showBottomDialog() {
        for (fragment in requireActivity().supportFragmentManager.fragments) {
            if (fragment is MovieBottomSheetFragment) return
        }
        val modalSheet = MovieBottomSheetFragment()
        modalSheet.show(requireActivity().supportFragmentManager, TAG)
        modalSheet.isCancelable = false
    }

    private fun openWebPage(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }
}