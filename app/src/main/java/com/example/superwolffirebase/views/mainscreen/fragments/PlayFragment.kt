package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superwolffirebase.R
import com.example.superwolffirebase.adapter.MessageAdapter
import com.example.superwolffirebase.adapter.PlayAdapter
import com.example.superwolffirebase.databinding.FragmentPlayBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.hideKeyboard
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment : Fragment() {
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<PlayFragmentArgs>()
    private val viewModel by viewModels<PlayViewModel>()
    private lateinit var playAdapter: PlayAdapter
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)


        binding.getback.setOnClickListener {
            viewModel.leaveRoom(args.room.name!!, args.room.amount!!, args.player.id!!)
        }

        binding.send.setOnClickListener {
            if (binding.etMessage.text.isNullOrBlank()){
                return@setOnClickListener
            } else {
                val message = binding.etMessage.text.toString()
                viewModel.sendMessage(args.room.name!!, message, args.player.name!!)
            }
        }

        viewModel.sendMessResult.observe(viewLifecycleOwner){resource->
            when(resource){
                is Resource.Success -> {
                    hideKeyboard()
                    binding.etMessage.setText("")
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }



        setUpPlayerList()

        setUpViewModel()




        return binding.root
    }



    private fun setUpViewModel(){
        viewModel.getAllPlayers(args.room.name!!)
        viewModel.getAllMessages(args.room.name!!)

        viewModel.allMessage.observe(viewLifecycleOwner){event->
            if (!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let{resource->
                    when(resource){
                        is Resource.Success -> {
                            resource.result.let {
                               messageAdapter.setData(it)
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }

                }
            }
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

        viewModel.allPlayer.observe(viewLifecycleOwner){event->
            if (!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let{resource->
                    when(resource){
                        is Resource.Success -> {
                            resource.result.let {
                                playAdapter.setData(it)
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }

                }
            }
        }
    }

    private fun setUpPlayerList(){
        playAdapter = PlayAdapter(requireContext())
        binding.playerList.layoutManager = GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
        binding.playerList.adapter = playAdapter
        binding.playerList.setHasFixedSize(true)

        messageAdapter = MessageAdapter()
        binding.messageList.layoutManager = LinearLayoutManager(requireContext())
        binding.messageList.adapter = messageAdapter

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}