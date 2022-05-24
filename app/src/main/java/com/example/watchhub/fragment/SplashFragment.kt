package com.example.watchhub.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.watchhub.R
import com.example.watchhub.databinding.FragmentSplashBinding
import com.example.watchhub.utils.FirebaseUtils.firebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        val acct = GoogleSignIn.getLastSignedInAccount(activity!!)
        binding.profileImage.alpha = 0f
        binding.profileImage.animate().setDuration(2200).alpha(1f).withEndAction {
            if (firebaseUser != null || acct != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else{
                findNavController().navigate(R.id.action_splashFragment_to_registerOrSignUpFragment)
            }
        }
    }
}