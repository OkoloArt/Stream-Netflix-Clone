package com.example.watchhub.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchhub.R
import com.example.watchhub.adapter.MoviePagerAdapter
import com.example.watchhub.databinding.FragmentHomeBinding
import com.example.watchhub.utils.ConnectivityLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess


class HomeFragment : Fragment() {

    private val categoryArray = listOf(
        "Movies",
        "Tv Shows"
    )

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: MoviePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkForInternet(requireContext())) {
            binding.noConnection.visibility = View.INVISIBLE
            binding.viewPager.visibility = View.VISIBLE
        } else {
            binding.noConnection.visibility = View.VISIBLE
            binding.viewPager.visibility = View.GONE
            Handler().postDelayed({
                Toast.makeText(
                    requireContext(), "Not Connected to a Network", Toast.LENGTH_SHORT).show()
            },300)
        }

        binding.retry.setOnClickListener {
            if (checkForInternet(requireContext())) {
                binding.noConnection.visibility = View.INVISIBLE
                binding.viewPager.visibility = View.VISIBLE
                binding.viewPager.currentItem = binding.tabs.selectedTabPosition
            } else {
                binding.noConnection.visibility = View.VISIBLE
                binding.viewPager.visibility = View.GONE
                Toast.makeText(requireContext(), "Please connect to a Network", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity!!.finish();
                    exitProcess(0);
                }
            })

        val acct = GoogleSignIn.getLastSignedInAccount(activity!!)
        if (acct != null) {
            val personName = acct.familyName
            binding.displayName.text = personName
        }

        binding.profileImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        pagerAdapter = MoviePagerAdapter(requireActivity())
        binding.apply {
            viewPager.adapter = pagerAdapter
            viewPager.isUserInputEnabled = false
            //tabs.setSelectedTabIndicator(null)
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = categoryArray[position]
        }.attach()
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




