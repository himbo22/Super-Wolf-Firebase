package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.FragmentMainScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpFragmentAction()

        return view
    }

    private fun setUpFragmentAction(){
        binding.apply {
            playButton.setOnClickListener {
                findNavController().navigate(R.id.action_mainScreenFragment_to_playFragment2)
            }
            profileButton.setOnClickListener{
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