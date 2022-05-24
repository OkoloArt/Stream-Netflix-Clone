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
import com.example.watchhub.adapter.TvShowRecyclerAdapter
import com.example.watchhub.database.MovieApplication
import com.example.watchhub.databinding.FragmentTvShowsBinding
import com.example.watchhub.model.TvShowResults
import com.example.watchhub.model.TvShows
import com.example.watchhub.network.MovieApiInterface
import com.example.watchhub.network.RetrofitInstance
import com.example.watchhub.tvShowDatabase.TvDatabaseViewModel
import com.example.watchhub.tvShowDatabase.TvDatabaseViewModelFactory
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

/**
 * A simple [Fragment] subclass.
 * Use the [TvShowsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TvShowsFragment : Fragment() {

    val tvShowViewModel: MovieViewModel by activityViewModels()

    private val tvDatabaseViewModel: TvDatabaseViewModel by activityViewModels {
        TvDatabaseViewModelFactory((activity?.application as MovieApplication).tvdatabase.testDao())
    }

    private var _binding: FragmentTvShowsBinding? = null
    private val binding get() = _binding!!

    private var popularTvShowResults = mutableListOf<TvShowResults>(
    )
    var singleTvShowsOnAirResultsList = mutableListOf<TvShowResults>(
    )
    var topRatedTvShowsResultsList = mutableListOf<TvShowResults>(
    )
    var airingTodayTvShowsResultsList = mutableListOf<TvShowResults>(
    )

    private lateinit var adapter: TvShowRecyclerAdapter

    private var movieName = ""
    private var movieId = 0
    private var movieImage = ""
    val webUrl = "https://imdbembed.xyz/tv/tmdb/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTvShowsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityLiveData = ConnectivityLiveData(activity!!.application)
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
//                    retrieveTvShowOnAirJson(API_KEY, LANGUAGE, PAGES)
                    retrievePopularTvShowJson(API_KEY, LANGUAGE, PAGES)
                    retrieveTopRatedTvShowJson(API_KEY, LANGUAGE, PAGES)
                    retrieveAiringTodayTvShowJson(API_KEY, LANGUAGE, PAGES)
                    Timer().scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            retrieveTvShowOnAirJson(API_KEY, LANGUAGE, PAGES)
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


    private fun retrievePopularTvShowJson(apiKey: String?, language: String?, pages: Int) {
        val movieCall: Call<TvShows?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getPopularTvShows(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<TvShows?> {
            override fun onResponse(call: Call<TvShows?>?, response: Response<TvShows?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    binding.popularProgressBar.visibility = View.INVISIBLE
                    popularTvShowResults.clear()
                    popularTvShowResults = response.body()?.results as MutableList<TvShowResults>
                    adapter = TvShowRecyclerAdapter(popularTvShowResults) {
                        tvShowViewModel.setMovieName(it.tvTitle)
                        tvShowViewModel.setMovieId(it.tvId)
                        tvShowViewModel.setMovieImage(it.tvPosterPath)
                        tvShowViewModel.setMovieOverview(it.tvOverview)
                        showBottomDialog()
                    }
                    binding.tvShowPopularRecyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    binding.tvShowPopularRecyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<TvShows?>?, t: Throwable) {
            }
        })
    }

    private fun retrieveAiringTodayTvShowJson(apiKey: String?, language: String?, pages: Int) {
        val movieCall: Call<TvShows?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getTvShowsAiringToday(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<TvShows?> {
            override fun onResponse(call: Call<TvShows?>?, response: Response<TvShows?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    binding.airingTodayProgressBar.visibility = View.INVISIBLE
                    airingTodayTvShowsResultsList.clear()
                    airingTodayTvShowsResultsList =
                        response.body()?.results as MutableList<TvShowResults>
                    adapter = TvShowRecyclerAdapter(airingTodayTvShowsResultsList) {
                        tvShowViewModel.setMovieName(it.tvTitle)
                        tvShowViewModel.setMovieId(it.tvId)
                        tvShowViewModel.setMovieImage(it.tvPosterPath)
                        tvShowViewModel.setMovieOverview(it.tvOverview)
                        showBottomDialog()
                    }
                    binding.airingTodayRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    binding.airingTodayRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<TvShows?>?, t: Throwable) {
            }
        })
    }

    private fun retrieveTopRatedTvShowJson(apiKey: String?, language: String?, pages: Int) {
        val movieCall: Call<TvShows?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getTopRatedTvShows(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<TvShows?> {
            override fun onResponse(call: Call<TvShows?>?, response: Response<TvShows?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    binding.topratedProgressBar.visibility = View.INVISIBLE
                    topRatedTvShowsResultsList.clear()
                    topRatedTvShowsResultsList =
                        response.body()?.results as MutableList<TvShowResults>
                    adapter = TvShowRecyclerAdapter(topRatedTvShowsResultsList) {
                        tvShowViewModel.setMovieName(it.tvTitle)
                        tvShowViewModel.setMovieId(it.tvId)
                        tvShowViewModel.setMovieImage(it.tvPosterPath)
                        tvShowViewModel.setMovieOverview(it.tvOverview)
                        showBottomDialog()
                    }
                    binding.topRatedRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    binding.topRatedRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<TvShows?>?, t: Throwable) {
            }
        })
    }

    private fun retrieveTvShowOnAirJson(apiKey: String?, language: String?, pages: Int) {
        val movieCall: Call<TvShows?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getTvShowsOnAir(apiKey, language, pages)
        movieCall?.enqueue(object : Callback<TvShows?> {
            override fun onResponse(call: Call<TvShows?>, response: Response<TvShows?>) {
                if (response.isSuccessful && response.body() != null) {

                    singleTvShowsOnAirResultsList.clear()
                    singleTvShowsOnAirResultsList =
                        response.body()?.results as MutableList<TvShowResults>

                    checkUser()
                    val randomIndex = Random.nextInt(singleTvShowsOnAirResultsList.size)
                    //   binding.singleTvshowTitle.text = singleTvShowsOnAirResultsList[randomIndex].tvTitle
                    val image =
                        "https://image.tmdb.org/t/p/w342${singleTvShowsOnAirResultsList[randomIndex].tvPosterPath}"
                    Picasso.get().load(image).into(binding.singleTvshowImage)
                    movieName = singleTvShowsOnAirResultsList[randomIndex].tvTitle
                    movieImage = singleTvShowsOnAirResultsList[randomIndex].tvPosterPath
                    movieId = singleTvShowsOnAirResultsList[randomIndex].tvId
                    checkUser()
                    binding.info.setOnClickListener {
                        tvShowViewModel.setMovieName(singleTvShowsOnAirResultsList[randomIndex].tvTitle)
                        tvShowViewModel.setMovieImage(singleTvShowsOnAirResultsList[randomIndex].tvPosterPath)
                        tvShowViewModel.setMovieOverview(singleTvShowsOnAirResultsList[randomIndex].tvOverview)
                        tvShowViewModel.setMovieId(singleTvShowsOnAirResultsList[randomIndex].tvId)
                        showBottomDialog()
                    }
                    val id = singleTvShowsOnAirResultsList[randomIndex].tvId
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
                                openWebPage("$webUrl${id}-1-1")
                            }
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<TvShows?>, t: Throwable) {
            }

        })
    }

    private fun addFavouriteMovies() {
        runBlocking {
            launch {
                val user = tvDatabaseViewModel.checkUser(movieName, movieId)
                if (isEntryValid()) {
                    if (user != null) {
                        Toast.makeText(
                            activity,
                            "Movie Already Added To Favourite",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        tvDatabaseViewModel.addNewItem(
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
                val user = tvDatabaseViewModel.checkUser(movieName, movieId)
                if (isEntryValid()) {
                    if (user != null) {
                        tvDatabaseViewModel.deleteItem(movieName)
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
                val user = tvDatabaseViewModel.checkUser(movieName, movieId)
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
        return tvDatabaseViewModel.isEntryValid(
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
            if (fragment is TvBottomSheetFragment) return
        }
        val modalSheet = TvBottomSheetFragment()
        modalSheet.show(requireActivity().supportFragmentManager, TvBottomSheetFragment.TAG)
        modalSheet.isCancelable = false
    }

    private fun openWebPage(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }
}