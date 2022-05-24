package com.example.watchhub.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchhub.viewmodel.MovieViewModel
import com.example.watchhub.adapter.UpcomingRecyclerAdapter
import com.example.watchhub.databinding.FragmentUpcomingBinding
import com.example.watchhub.model.Movie
import com.example.watchhub.model.MovieResults
import com.example.watchhub.network.MovieApiInterface
import com.example.watchhub.network.RetrofitInstance
import com.example.watchhub.utils.ConnectivityLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [UpcomingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpcomingFragment : Fragment() {

    val movieViewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!


    var movieResultsList = mutableListOf<MovieResults>(
    )
    private lateinit var adapter: UpcomingRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpcomingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkForInternet(requireContext())) {
            binding.upcomingRecyclerview.visibility = View.VISIBLE
            binding.upcomingProgressBar.visibility=View.VISIBLE
            binding.noConnection.visibility = View.GONE
        } else {
            binding.noConnection.visibility = View.VISIBLE
            binding.upcomingRecyclerview.visibility = View.INVISIBLE
        }

        val connectivityLiveData = ConnectivityLiveData(activity!!.application)
        connectivityLiveData.observe(this, { isAvailable ->
            when (isAvailable) {
                true -> {
                    binding.upcomingRecyclerview.visibility = View.VISIBLE
                    binding.upcomingProgressBar.visibility=View.VISIBLE
                    retrieveUpcomingMovieJson(API_KEY, LANGUAGE, REGION, RELEASE_TYPE)
                    binding.noConnection.visibility = View.GONE
                }
                false -> {
                    binding.noConnection.visibility = View.VISIBLE
                    binding.upcomingRecyclerview.visibility = View.INVISIBLE
//                    if (findNavController().currentDestination?.id == R.id.upcomingFragment) {
//                        findNavController().navigate(R.id.action_upcomingFragment_self)
//                    }
                }
            }

        })
        binding.retry.setOnClickListener {
            if (checkForInternet(requireContext())) {
                binding.upcomingRecyclerview.visibility = View.VISIBLE
                retrieveUpcomingMovieJson(API_KEY, LANGUAGE, REGION, RELEASE_TYPE)
                binding.noConnection.visibility = View.GONE
            }else{
//                binding.noConnection.visibility=View.VISIBLE
//                binding.upcomingRecyclerview.visibility=View.INVISIBLE
                Toast.makeText(requireContext(),"Please connect to a Network", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun retrieveUpcomingMovieJson(
        apiKey: String?,
        language: String?,
        region: String,
        release_type: String
    ) {
        val movieCall: Call<Movie?>? = RetrofitInstance.retrofitInstance!!.create(
            MovieApiInterface::class.java
        ).getUpcomingMovies(apiKey, language, region, release_type)
        movieCall?.enqueue(object : Callback<Movie?> {
            override fun onResponse(call: Call<Movie?>?, response: Response<Movie?>) {
                if (response.isSuccessful && response.body()?.results != null) {
                    binding.upcomingProgressBar.visibility=View.INVISIBLE
                    movieResultsList.clear()
                    movieResultsList = response.body()?.results as MutableList<MovieResults>

                    adapter = UpcomingRecyclerAdapter(movieResultsList) {
                    }
                    binding.upcomingRecyclerview.layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.upcomingRecyclerview.adapter = adapter
                }
            }

            override fun onFailure(call: Call<Movie?>?, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val LANGUAGE = "en-US"
        private const val API_KEY = "7af2394d40b06bda9cd7d96cb7a29d3f"
        private const val REGION = "CA|US"
        private const val RELEASE_TYPE = "2|3"
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
