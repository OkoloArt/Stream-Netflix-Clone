package com.example.watchhub.fragment


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchhub.*
import com.example.watchhub.adapter.MovieCastAdapter
import com.example.watchhub.adapter.MovieRecyclerAdapter
import com.example.watchhub.database.MovieApplication
import com.example.watchhub.database.MovieDatabaseViewModel
import com.example.watchhub.database.MovieDatabaseViewModelFactory
import com.example.watchhub.databinding.FragmentDetailBinding
import com.example.watchhub.model.*
import com.example.watchhub.network.MovieApiInterface
import com.example.watchhub.network.RetrofitInstance
import com.example.watchhub.utils.ConnectivityLiveData
import com.example.watchhub.viewmodel.MovieViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {

    private val movieViewModel: MovieViewModel by activityViewModels()
    private val movieDatabaseViewModel: MovieDatabaseViewModel by activityViewModels {
        MovieDatabaseViewModelFactory((activity?.application as MovieApplication).database.testDao())
    }

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    var castResultsList = mutableListOf<CastResults>(
    )
    var recommendedResultsList = mutableListOf<MovieResults>(
    )
    var trailerResultsList = mutableListOf<TrailerResults>(
    )
    private lateinit var castAdapter: MovieCastAdapter
    private lateinit var recommendAdapter: MovieRecyclerAdapter
    private val webUrl = "https://imdbembed.xyz/movie/tmdb/"
    var refreshVideoId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
                }
            })

        val connectivityLiveData = ConnectivityLiveData(activity!!.application)
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    binding.cvOneLogin.visibility = View.VISIBLE
                    retrieveMovieDetailsJson(movieViewModel.movieId, API_KEY, LANGUAGE)
                    retrieveMovieTrailerJson(movieViewModel.movieId, API_KEY, LANGUAGE)
                    retrieveMovieGenreDetailsJson(movieViewModel.movieId, API_KEY, LANGUAGE)
                    retrieveMovieCast(movieViewModel.movieId, API_KEY, LANGUAGE)
                    retrieveRecommendedMovieJson(
                        movieViewModel.movieId,
                        API_KEY,
                        LANGUAGE,
                        PAGES
                    )
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
                findNavController().navigate(R.id.action_detailFragment_to_homeFragment)
            }
            myList.setOnClickListener {
                addFavouriteMovies()
            }
            removeList.setOnClickListener {
                deleteFromFavourite()
            }
            playButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.title))
                    .setMessage(resources.getString(R.string.supporting_text))
                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                        // Respond to negative button press
                        dialog.cancel()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        // Respond to positive button press
                        openWebPage("$webUrl${movieViewModel.movieId}")
                    }
                    .show()
            }
        }
    }

    private fun addFavouriteMovies() {
        val movieName = movieViewModel.movieName
        val movieId = movieViewModel.movieId
        val movieImage = movieViewModel.movieImage
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
                        Toast.makeText(activity, "Successfully Added", Toast.LENGTH_SHORT)
                            .show()
                        binding.myList.visibility = View.INVISIBLE
                        binding.removeList.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun deleteFromFavourite() {
        val movieName = movieViewModel.movieName
        val movieId = movieViewModel.movieId
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
        val movieName = movieViewModel.movieName
        val movieId = movieViewModel.movieId
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
            movieViewModel.movieImage,
            movieViewModel.movieId
        )
    }

    private fun retrieveMovieDetailsJson(
        id: Int,
        apiKey: String?,
        language: String?
    ) {

        val movieCall: Call<MovieResults?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getMovieDetails(id, apiKey, language)
        movieCall?.enqueue(object : Callback<MovieResults?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<MovieResults?>,
                response: Response<MovieResults?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    binding.detailsProgressBar.visibility = View.INVISIBLE
                    binding.detailLayout.visibility = View.VISIBLE
                    binding.movieTitle.text = response.body()!!.movieTitle
                    binding.movieRating.text = response.body()!!.movieVoteAverage.toString()
                    binding.movieOverview.text = response.body()!!.movieOverview
                    binding.movieTagLine.text = response.body()!!.movieTagLine
                    binding.movieYear.text = dateDay(response.body()!!.movieReleaseDate)
                    binding.movieTime.text = convertMinutes(response.body()!!.movieRuntime)
                    binding.share.setOnClickListener {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, response.body()!!.movieHomePage)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                }
            }

            override fun onFailure(call: Call<MovieResults?>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveMovieTrailerJson(
        id: Int,
        apiKey: String?,
        language: String?
    ) {
        val movieCall: Call<Trailers?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getMovieTrailer(id, apiKey, language)
        movieCall?.enqueue(object : Callback<Trailers?> {
            override fun onResponse(
                call: Call<Trailers?>,
                response: Response<Trailers?>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    trailerResultsList.clear()
                    trailerResultsList =
                        response.body()?.trailers as MutableList<TrailerResults>
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
                            refreshVideoId = trailerResultsList[i].trailerKey
                            lifecycle.addObserver(binding.youtubePlayerView)
                            binding.youtubePlayerView.addYouTubePlayerListener(object :
                                AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    val videoId = trailerResultsList[i].trailerKey
                                    youTubePlayer.loadVideo(refreshVideoId, 0f)
                                }
                            })
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Trailers?>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveMovieGenreDetailsJson(
        id: Int,
        apiKey: String?,
        language: String?
    ) {
        val movieCall: Call<Genres?>? =
            RetrofitInstance.retrofitInstance!!.create(MovieApiInterface::class.java)
                .getMovieGenre(id, apiKey, language)
        movieCall?.enqueue(object : Callback<Genres?> {
            override fun onResponse(
                call: Call<Genres?>,
                response: Response<Genres?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val genreList = response.body()!!.genres
                    if (genreList.size > 1) {
                        binding.movieGenreOne.text = genreList[0].genreName
                        binding.movieGenreOne.text = genreList[1].genreName
                    } else {
                        for (i in genreList.indices) {
                            binding.movieGenreOne.text = genreList[i].genreName
                            binding.movieGenreOne.text = ""
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Genres?>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveMovieCast(
        movieId: Int?,
        apiKey: String?,
        language: String?
    ) {
        val movieCall: Call<Cast?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getCast(movieId, apiKey, language)
        movieCall?.enqueue(object : Callback<Cast?> {
            override fun onResponse(call: Call<Cast?>?, response: Response<Cast?>) {
                if (response.isSuccessful && response.body()?.cast != null) {
                    castResultsList.clear()
                    castResultsList = response.body()?.cast as MutableList<CastResults>
                    castAdapter = MovieCastAdapter(castResultsList) {

                    }
                    binding.castRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL, false
                    )
                    binding.castRecyclerview.adapter = castAdapter
                }
            }

            override fun onFailure(call: Call<Cast?>?, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveRecommendedMovieJson(
        movieId: Int?,
        apiKey: String?,
        language: String?,
        pages: Int?
    ) {
        val movieCall: Call<Movie?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getRecommendations(movieId, apiKey, language, pages)
        movieCall?.enqueue(object : Callback<Movie?> {
            override fun onResponse(call: Call<Movie?>?, response: Response<Movie?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    recommendedResultsList.clear()
                    recommendedResultsList =
                        response.body()?.results as MutableList<MovieResults>
                    recommendAdapter = MovieRecyclerAdapter(recommendedResultsList) {
                        movieViewModel.setMovieName(it.movieTitle)
                        movieViewModel.setMovieId(it.movieId)
                        movieViewModel.setMovieImage(it.moviePosterPath)
                        movieViewModel.setMovieOverview(it.movieOverview)
                        movieViewModel.setMovieRating(it.movieVoteAverage)
                        showBottomDialog()
                    }
                    binding.recommendedMoviesReyclerview.layoutManager = GridLayoutManager(
                        requireContext(),
                        3,
                    )
                    binding.recommendedMoviesReyclerview.adapter = recommendAdapter
                }
            }

            override fun onFailure(call: Call<Movie?>?, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private val LANGUAGE = "en-US"
        private val API_KEY = "7af2394d40b06bda9cd7d96cb7a29d3f"
        private val PAGES = 1
    }

    private fun showBottomDialog() {
        for (fragment in requireActivity().supportFragmentManager.fragments) {
            if (fragment is MovieBottomSheetFragment) return
        }
        val modalSheet = MovieBottomSheetFragment()
        modalSheet.show(
            requireActivity().supportFragmentManager,
            MovieBottomSheetFragment.TAG
        )
        modalSheet.isCancelable = false
    }

    private fun convertMinutes(time: Int): String {
        val hours: Int = time / 60
        val minutes: Int = time % 60

        return "$hours" + "h " + "$minutes" + "m"
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