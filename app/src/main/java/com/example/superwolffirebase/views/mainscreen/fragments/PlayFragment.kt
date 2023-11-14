package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.FragmentPlayBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment : Fragment() {
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<PlayFragmentArgs>()
    private val viewModel by viewModels<PlayViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)


        binding.btn.setOnClickListener {
            viewModel.leaveRoom(args.room.name!!, args.room.amount!!, args.player.id!!)
        }

        viewModel.leaveRoomResult.observe(viewLifecycleOwner) { event ->
            if (!event?.hasBeenHandled!!) {
                event.getContentIfNotHandled()?.let{resource->
                    when(resource){

                        is Resource.Success -> {
                            val action = PlayFragmentDirections.actionPlayFragmentToLobbyFragment(args.player)
                            findNavController().navigate(action)
                        }

                        else -> {}
                    }

                }
            }
        }


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}