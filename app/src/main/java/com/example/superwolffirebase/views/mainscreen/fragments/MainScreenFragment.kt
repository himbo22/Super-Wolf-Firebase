package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.FragmentMainScreenBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.MainScreenViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainScreenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpFragmentAction()
        setUpObserveViewModel()


        return view
    }

    private fun setUpObserveViewModel(){
        viewModel.completeProfile.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    enableButton(true)
                    binding.rlLoading.invisible()
                }

                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resource.exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    enableButton(false)
                    binding.rlLoading.invisible()
                }
                is Resource.Loading -> {
                    binding.rlLoading.show()
                }
            }
        }
    }

    private fun enableButton(enable: Boolean){
        if(enable){
            binding.apply {
                playButton.isEnabled = enable
                playButton.isClickable = enable
                friendsButton.isEnabled = enable
                friendsButton.isClickable = enable
            }
        } else {
            binding.apply {
                playButton.isEnabled = enable
                playButton.isClickable = enable
                friendsButton.isEnabled = enable
                friendsButton.isClickable = enable
            }
        }
    }

    private fun setUpFragmentAction() {
        binding.apply {
            playButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainScreenFragment_to_playFragment2)
            }
            profileButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainScreenFragment_to_profileFragment)
            }
            friendsButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainScreenFragment_to_friendsFragment)
            }
            settingButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainScreenFragment_to_settingFragment)
            }
            aboutButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainScreenFragment_to_aboutFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}