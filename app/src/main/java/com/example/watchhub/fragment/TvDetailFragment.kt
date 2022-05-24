package com.example.watchhub.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchhub.viewmodel.MovieViewModel
import com.example.watchhub.R
import com.example.watchhub.adapter.EpisodeAdapter
import com.example.watchhub.adapter.TvShowRecyclerAdapter
import com.example.watchhub.database.MovieApplication
import com.example.watchhub.databinding.FragmentTvDetailBinding
import com.example.watchhub.model.*
import com.example.watchhub.network.MovieApiInterface
import com.example.watchhub.network.RetrofitInstance
import com.example.watchhub.tvShowDatabase.TvDatabaseViewModel
import com.example.watchhub.tvShowDatabase.TvDatabaseViewModelFactory
import com.example.watchhub.utils.ConnectivityLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

class TvDetailFragment : Fragment() {

    private val tvShowViewModel: MovieViewModel by activityViewModels()
    private val tvDatabaseViewModel: TvDatabaseViewModel by activityViewModels {
        TvDatabaseViewModelFactory((activity?.application as MovieApplication).tvdatabase.testDao())
    }

    private var _binding: FragmentTvDetailBinding? = null
    private val binding get() = _binding!!

    var episodeDetailList = mutableListOf<EpisodeDetails>(
    )
    var similarTvDetailList = mutableListOf<TvShowResults>(
    )
    var trailerResultsList = mutableListOf<TrailerResults>(
    )

    private val webUrl = "https://imdbembed.xyz/tv/tmdb/"
    var testSeasonNumber = 0

    val languages = mutableListOf<String>()
    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var similaradapter: TvShowRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTvDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_tvDetailFragment_to_homeFragment)
                }
            })

        val connectivityLiveData = ConnectivityLiveData(activity!!.application)
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    binding.cvOneLogin.visibility = View.VISIBLE
                    retrieveEpisodeDetailsJson(tvShowViewModel.movieId, 1, API_KEY, LANGUAGE)
                    retrieveTvDetailsJson(tvShowViewModel.movieId, API_KEY, LANGUAGE)
                    retrieveTvTrailerJson(tvShowViewModel.movieId, API_KEY, LANGUAGE)
                    retrieveTvShowGenreDetailsJson(tvShowViewModel.movieId, API_KEY, LANGUAGE)
                    binding.noConnection.visibility = View.GONE
                }
                false -> {
                    binding.cvOneLogin.visibility = View.INVISIBLE
                    binding.noConnection.visibility = View.VISIBLE
                }
            }
        })

        checkUser()

        binding.apply {
            back.setOnClickListener {
                findNavController().navigate(R.id.action_tvDetailFragment_to_homeFragment)
            }
        }
        binding.myList.setOnClickListener {
            addFavouriteMovies()
        }
        binding.removeList.setOnClickListener {
            deleteFromFavourite()
        }
        binding.episodes.setOnClickListener {
            binding.numberOfSeason.visibility = View.VISIBLE
            retrieveEpisodeDetailsJson(tvShowViewModel.movieId, 1, API_KEY, LANGUAGE)
        }
        binding.moreLikeThis.setOnClickListener {
            binding.numberOfSeason.visibility = View.GONE
            retrieveSimilarTvJson(tvShowViewModel.movieId, API_KEY, LANGUAGE, PAGES)
        }

    }

    private fun retrieveTvShowGenreDetailsJson(id: Int, apiKey: String?, language: String?) {
        val movieCall: Call<Genres?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getTvShowGenre(id, apiKey, language)
        movieCall?.enqueue(object : Callback<Genres?> {
            override fun onResponse(
                call: Call<Genres?>,
                response: Response<Genres?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val genreList = response.body()!!.genres
                    if (genreList.size > 1) {
                        binding.tvShowGenreOne.text = genreList[0].genreName
                        binding.tvShowGenre.text = genreList[1].genreName
                    } else {
                        for (i in genreList.indices) {
                            binding.tvShowGenreOne.text = genreList[i].genreName
                            binding.tvShowGenre.text = ""
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Genres?>, t: Throwable) {
            }
        })
    }

    private fun retrieveTvTrailerJson(id: Int, apiKey: String?, language: String?) {
        val movieCall: Call<Trailers?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getTvShowTrailers(id, apiKey, language)
        movieCall?.enqueue(object : Callback<Trailers?> {
            override fun onResponse(
                call: Call<Trailers?>,
                response: Response<Trailers?>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    trailerResultsList.clear()
                    trailerResultsList = response.body()?.trailers as MutableList<TrailerResults>
                    val names = listOf(
                        "Official Trailer",
                        "Official Trailer 1",
                        "Main Trailer",
                        "Official Trailer [Subtitled]"
                    )
                    for (i in trailerResultsList.indices) {
                        val foundValue =
                            names.find { item -> item == trailerResultsList[i].trailerName }
                        if (foundValue != null) {
                            lifecycle.addObserver(binding.youtubePlayerView)
                            binding.youtubePlayerView.addYouTubePlayerListener(object :
                                AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    val videoId = trailerResultsList[i].trailerKey
                                    youTubePlayer.loadVideo(videoId, 0f)
                                }
                            })
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Trailers?>, t: Throwable) {
            }
        })
    }

    private fun retrieveTvDetailsJson(id: Int?, apiKey: String?, language: String?) {
        val movieCall: Call<TvShowResults?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getTvShowDetail(id, apiKey, language)
        movieCall?.enqueue(object : Callback<TvShowResults?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<TvShowResults?>,
                response: Response<TvShowResults?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    binding.tvDetailsProgressBar.visibility = View.INVISIBLE
                    binding.tvDetailsLayout.visibility=View.VISIBLE
                    languages.clear()
                    binding.tvShowTitle.text = response.body()!!.tvTitle
                    binding.tvShowRating.text = response.body()!!.tvShowVoteAverage.toString()
                    if (response.body()!!.tvOverview.isNotEmpty()) {
                        binding.tvShowOverview.text = response.body()!!.tvOverview
                    } else {
                        binding.tvShowOverview.text = resources.getString(R.string.nullContent)
                    }
                    binding.tvShowYear.text = dateDay(response.body()!!.firstAirDate)
                    binding.tvShowTagLine.text = response.body()!!.tagLine
                    binding.share.setOnClickListener {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, response.body()!!.homepage)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                    val seasonNumber = response.body()!!.tvNumberOfSeasons
                    for (i in 1 until seasonNumber + 1) {
                        languages.add("Season $i")
                    }
                    val adapter =
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            languages
                        )
                    binding.numberOfSeason.adapter = adapter

                    binding.numberOfSeason.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                val newSeasonNumber = p2 + 1
                                testSeasonNumber = p2 + 1
                                retrieveEpisodeDetailsJson(
                                    tvShowViewModel.movieId,
                                    newSeasonNumber,
                                    API_KEY,
                                    LANGUAGE
                                )
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

                        }
                }
            }

            override fun onFailure(call: Call<TvShowResults?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun retrieveEpisodeDetailsJson(
        id: Int,
        seasonNumber: Int,
        apiKey: String?,
        language: String?
    ) {
        val movieCall: Call<EpisodeDetail?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getEpisodeDetail(id, seasonNumber, apiKey, language)
        movieCall?.enqueue(object : Callback<EpisodeDetail?> {
            override fun onResponse(
                call: Call<EpisodeDetail?>?,
                response: Response<EpisodeDetail?>
            ) {
                if (response.isSuccessful && response.body()?.episodes != null) {
                    episodeDetailList.clear()
                    episodeDetailList = response.body()?.episodes as MutableList<EpisodeDetails>

                    episodeAdapter = EpisodeAdapter(episodeDetailList) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.title))
                            .setMessage(resources.getString(R.string.supporting_text))
                            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                                // Respond to negative button press
                                dialog.cancel()
                            }
                            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                                // Respond to positive button press
                                openWebPage("$webUrl${tvShowViewModel.movieId}-$testSeasonNumber-${it.episodeNumber}")
                            }
                            .show()
                    }
                    binding.tvEpisodeRecyclerView.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.tvEpisodeRecyclerView.adapter = episodeAdapter
                }
            }

            override fun onFailure(call: Call<EpisodeDetail?>?, t: Throwable) {
            }
        })
    }

    private fun retrieveSimilarTvJson(
        movieId: Int?,
        apiKey: String?,
        language: String?,
        pages: Int
    ) {
        val movieCall: Call<TvShows?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getSimilarTvShows(movieId, apiKey, language, pages)
        movieCall?.enqueue(object : Callback<TvShows?> {
            override fun onResponse(call: Call<TvShows?>?, response: Response<TvShows?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    similarTvDetailList.clear()
                    similarTvDetailList = response.body()?.results as MutableList<TvShowResults>
                    similaradapter = TvShowRecyclerAdapter(similarTvDetailList) {
                        tvShowViewModel.setMovieName(it.tvTitle)
                        tvShowViewModel.setMovieId(it.tvId)
                        tvShowViewModel.setMovieImage(it.tvPosterPath)
                        tvShowViewModel.setMovieOverview(it.tvOverview)
                        tvShowViewModel.setMovieRating(it.tvShowVoteAverage)
                        showBottomDialog()
                    }
                    binding.tvEpisodeRecyclerView.layoutManager = GridLayoutManager(
                        requireContext(),
                        3,
                    )
                    binding.tvEpisodeRecyclerView.adapter = similaradapter
                }
            }

            override fun onFailure(call: Call<TvShows?>?, t: Throwable) {
            }
        })
    }

    private fun addFavouriteMovies() {
        val movieName = tvShowViewModel.movieName
        val movieId = tvShowViewModel.movieId
        val movieImage = tvShowViewModel.movieImage
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
        val movieName = tvShowViewModel.movieName
        val movieId = tvShowViewModel.movieId
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
        val movieName = tvShowViewModel.movieName
        val movieId = tvShowViewModel.movieId
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
            tvShowViewModel.movieImage,
            tvShowViewModel.movieId
        )
    }

    companion object {
        private const val LANGUAGE = "en-US"
        private const val API_KEY = "7af2394d40b06bda9cd7d96cb7a29d3f"
        private const val PAGES = 1

    }

    fun showBottomDialog() {
        for (fragment in requireActivity().supportFragmentManager.fragments) {
            if (fragment is TvBottomSheetFragment) return
        }
        val modalSheet = TvBottomSheetFragment()
        modalSheet.show(requireActivity().supportFragmentManager, TvBottomSheetFragment.TAG)
        modalSheet.isCancelable = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dateDay(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = formatter.parse(dateString)
        return DateTimeFormatter.ofPattern("yyyy").format(date)
    }

    private fun openWebPage(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }
}