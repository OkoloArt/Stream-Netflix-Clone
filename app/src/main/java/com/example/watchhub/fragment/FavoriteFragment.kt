package com.example.watchhub.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.watchhub.viewmodel.MovieViewModel
import com.example.watchhub.R
import com.example.watchhub.adapter.MovieDatabaseRecyclerAdapter
import com.example.watchhub.adapter.TvDatabaseRecyclerAdapter
import com.example.watchhub.database.MovieApplication
import com.example.watchhub.database.MovieDatabaseViewModel
import com.example.watchhub.database.MovieDatabaseViewModelFactory
import com.example.watchhub.databinding.FragmentFavoriteBinding
import com.example.watchhub.tvShowDatabase.TvDatabaseViewModel
import com.example.watchhub.tvShowDatabase.TvDatabaseViewModelFactory


class FavoriteFragment : Fragment() {

    private val favoriteViewModel: MovieViewModel by activityViewModels()
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val movieDatabaseViewModel: MovieDatabaseViewModel by activityViewModels {
        MovieDatabaseViewModelFactory((activity?.application as MovieApplication).database.testDao())
    }
    private val tvDatabaseViewModel: TvDatabaseViewModel by activityViewModels {
        TvDatabaseViewModelFactory((activity?.application as MovieApplication).tvdatabase.testDao())
    }

    private lateinit var movieAdapter: MovieDatabaseRecyclerAdapter
    private lateinit var tvShowadapter: TvDatabaseRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showMovieFavourite()
        binding.spinnerText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> showMovieFavourite()
                    1 -> showTvShowFavourite()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun showMovieFavourite() {
        movieAdapter = MovieDatabaseRecyclerAdapter {
            favoriteViewModel.setMovieName(it.movieName)
            favoriteViewModel.setMovieId(it.movieId)
            favoriteViewModel.setMovieImage(it.movieImage)
            if (checkForInternet(requireContext())) {
                findNavController().navigate( R.id.action_favoriteFragment_to_detailFragment)
            } else {
                Toast.makeText(requireContext(), "Please connect to a Network", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(this.context, 3)
        binding.favoriteRecyclerView.adapter = movieAdapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        movieDatabaseViewModel.allItems.observe(this@FavoriteFragment) { favourites ->
            favourites.let {
                movieAdapter.submitList(it)
            }
        }
    }

    private fun showTvShowFavourite() {
        tvShowadapter = TvDatabaseRecyclerAdapter {
            favoriteViewModel.setMovieName(it.tvShowName)
            favoriteViewModel.setMovieId(it.tvShowId)
            favoriteViewModel.setMovieImage(it.tvShowImage)
            if (checkForInternet(requireContext())) {
                findNavController().navigate(R.id.action_favoriteFragment_to_tvDetailFragment)
            } else {
                Toast.makeText(requireContext(), "Please connect to a Network", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(this.context, 3)
        binding.favoriteRecyclerView.adapter = tvShowadapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.

        tvDatabaseViewModel.allItems.observe(this@FavoriteFragment) { favourites ->
            favourites.let {
                tvShowadapter.submitList(it)
            }
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}