package com.example.watchhub.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.watchhub.viewmodel.MovieViewModel
import com.example.watchhub.R
import com.example.watchhub.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [MovieBottomSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovieBottomSheetFragment : BottomSheetDialogFragment() {

    private val movieViewModel: MovieViewModel by activityViewModels()

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webUrl = "https://imdbembed.xyz/movie/tmdb/"

        binding.apply {
            bottomMovieDescription.text = movieViewModel.movieOverview
            bottomMovieTitle.text = movieViewModel.movieName
            val image = "https://image.tmdb.org/t/p/w342${movieViewModel.movieImage}"
            Picasso.get().load(image).into(binding.bottomMovieImage)

            moreInfo.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                    val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
                    findNavController().navigate(action)
                }else{
                    val action = DetailFragmentDirections.actionDetailFragmentSelf()
                    findNavController().navigate(action)
                }
                dismiss()
            }
            closeDialog.setOnClickListener {
                dismiss()
            }
            val id = movieViewModel.movieId
            play.setOnClickListener {
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

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    private fun openWebPage(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }
}