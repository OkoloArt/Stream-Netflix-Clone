package com.example.watchhub

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.watchhub.databinding.ActivityMainBinding
import com.example.watchhub.fragment.FavoriteFragment
import com.example.watchhub.fragment.HomeFragment
import com.example.watchhub.fragment.UpcomingFragment


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Set up the action bar for use with the NavController

        navController.addOnDestinationChangedListener { _, destination, _ ->
           if (destination.id == R.id.homeFragment||destination.id == R.id.favoriteFragment||destination.id == R.id.upcomingFragment) {
                Handler().postDelayed({
                    //doSomethingHere()
                    binding.navView.visibility = View.VISIBLE
                }, 200)
               when (destination.id) {
                   R.id.homeFragment -> {
                       binding.navView.setItemSelected(R.id.home,true)
                   }
                   R.id.favoriteFragment -> {
                       binding.navView.setItemSelected(R.id.favorite,true)
                   }
                   else -> {
                       binding.navView.setItemSelected(R.id.coming_soon,true)
                   }
               }
            }else {
                   binding.navView.visibility = View.GONE
           }
        }

        binding.navView.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    when (findNavController(R.id.nav_host_fragment).currentDestination?.id) {
                        R.id.homeFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeFragment_self)
                        }
                        R.id.favoriteFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_favoriteFragment_to_homeFragment)
                        }
                        else -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_upcomingFragment_to_homeFragment)
                        }
                    }
                }
                R.id.favorite -> {
                    when (findNavController(R.id.nav_host_fragment).currentDestination?.id) {
                        R.id.favoriteFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_favoriteFragment_self)
                        }
                        R.id.homeFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_favoriteFragment)
                        }
                        else -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_upcomingFragment_to_favoriteFragment)
                        }
                    }
                }
                R.id.coming_soon -> {
                    when (findNavController(R.id.nav_host_fragment).currentDestination?.id) {
                        R.id.favoriteFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_favoriteFragment_to_upcomingFragment)
                        }
                        R.id.homeFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_homeFragment_to_upcomingFragment2)
                        }
                        else -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_upcomingFragment_self)
                        }
                    }
                }
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
