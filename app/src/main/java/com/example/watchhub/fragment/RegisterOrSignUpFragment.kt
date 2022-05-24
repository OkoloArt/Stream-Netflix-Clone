package com.example.watchhub.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.watchhub.R
import com.example.watchhub.databinding.FragmentRegisterOrSignUpBinding
import kotlin.system.exitProcess

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterOrSignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterOrSignUpFragment : Fragment() {

    private var _binding: FragmentRegisterOrSignUpBinding? = null
    private val binding get() = _binding!!

    val imageList = ArrayList<SlideModel>() // Create image list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterOrSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity!!.finish();
                    exitProcess(0);
                }
            })

        imageList.clear()
        imageList.add(SlideModel(R.drawable.avenger))
        imageList.add(SlideModel(R.drawable.slime))
        imageList.add(SlideModel(R.drawable.batman))

        binding.movieImage.setImageList(imageList, ScaleTypes.FIT)

        binding.apply {
            register.setOnClickListener {
                findNavController().navigate(R.id.action_registerOrSignUpFragment_to_registerFragment2)
            }
            signup.setOnClickListener {
                findNavController().navigate(R.id.action_registerOrSignUpFragment_to_signInFragment)
            }
        }
    }
}