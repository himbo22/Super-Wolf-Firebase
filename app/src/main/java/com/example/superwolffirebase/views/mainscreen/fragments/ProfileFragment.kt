package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.superwolffirebase.databinding.FragmentProfileBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        viewModel.playerInfo.observe(viewLifecycleOwner){event->
            if(!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {

                        is Resource.Loading -> {
                            binding.rlLoading.show()
                        }

                        is Resource.Success -> {
                            resource.result.let {
                                binding.apply {
                                    profileEmail.text = it.email
                                    profileName.text = it.name
                                    profileGender.text = it.gender
                                    Glide.with(requireView()).load(it.avatar).into(binding.profileAvatar)
                                    rlLoading.invisible()
                                }
                            }


                        }

                        is Resource.Error -> {
                            binding.rlLoading.invisible()
                        }


                        else -> {}
                    }
                }
            }
        }



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}