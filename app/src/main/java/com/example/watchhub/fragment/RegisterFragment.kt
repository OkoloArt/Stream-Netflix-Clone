package com.example.watchhub.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.watchhub.R
import com.example.watchhub.databinding.FragmentRegisterBinding
import com.example.watchhub.utils.FirebaseUtils.firebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_registerFragment2_to_registerOrSignUpFragment)
                }
            })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("969470895047-e0lfs8sf9ipb1e3vbgpenmv004s8a6si.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.signUpButton.setOnClickListener {
            if (checkForInternet(requireContext())) {
                register()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please Connect to a Network to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.logIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment2_to_signInFragment)
        }
        binding.googleButton.setOnClickListener {
            if (checkForInternet(requireContext())) {
                signInWithGoogle()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please Connect to a Network to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * It creates a new user with the email and password provided.
     */
    private fun register() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            binding.textInputLayout3.error = null
            binding.textInputLayout2.error = null
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_registerFragment2_to_registerOrSignUpFragment)
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
        }else{
            binding.textInputLayout2.error = "Email Cannot be Blank"
            binding.textInputLayout3.error = "Password Cannot be Blank"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google SignIn Successful
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)

            } catch (e: Exception) {
                //Failed Google Sign In
                Toast.makeText(requireContext(), "SignIn Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //Login success
                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()

                //check if user is new or existing
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Toast.makeText(requireContext(), "Account Created", Toast.LENGTH_SHORT).show()
                }
//                else {
//                    Toast.makeText(requireContext(), "User Already Exists", Toast.LENGTH_SHORT)
//                        .show()
//                }

                // Start Home Fragment
                findNavController().navigate(R.id.action_registerFragment2_to_homeFragment)
            }
            .addOnFailureListener {
                // Login Failed
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}