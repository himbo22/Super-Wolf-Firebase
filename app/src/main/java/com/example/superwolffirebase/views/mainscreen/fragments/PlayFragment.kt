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
import com.bumptech.glide.annotation.GlideModule
import com.example.superwolffirebase.R
import com.example.superwolffirebase.adapter.MessageAdapter
import com.example.superwolffirebase.adapter.OnItemClick
import com.example.superwolffirebase.adapter.PlayAdapter
import com.example.superwolffirebase.databinding.FragmentPlayBinding
import com.example.superwolffirebase.databinding.ItemPlayerBinding
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.hideKeyboard
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint


@GlideModule

@AndroidEntryPoint
class PlayFragment : Fragment(), OnItemClick {
    private var _binding: FragmentPlayBinding? = null
    private var _itemPlayerBinding: ItemPlayerBinding? = null
    private val binding get() = _binding!!
    private val itemPlayerBinding get() = _itemPlayerBinding!!
    private val args by navArgs<PlayFragmentArgs>()
    private val viewModel by viewModels<PlayViewModel>()
    private lateinit var playAdapter: PlayAdapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var playerInGame: PlayerInGame

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)






        setUpView()

        setUpViewModel()

        setUpPlayerList()





        return binding.root
    }

    private fun setUpView(){
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
    }



    private fun setUpViewModel(){
        viewModel.getAllPlayers(args.room.name!!)
        viewModel.getAllMessages(args.room.name!!)
        viewModel.getPlayerInGame(args.player.id!!, args.room.name!!)

        viewModel.votePlayer.observe(viewLifecycleOwner){event->
            if (!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let {resource ->
                    when(resource){
                        is Resource.Success -> {
                            Toast.makeText(requireContext(), playerInGame.vote, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }}

        viewModel.getPlayerInGame.observe(viewLifecycleOwner){event->
            if (!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let {resource ->
                    when(resource){
                        is Resource.Success -> {
                            playerInGame = resource.result
                        }

                        else -> {}
                    }
                }
            }
        }

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
        viewModel.sendMessResult.observe(viewLifecycleOwner){resource->
            when(resource){
                is Resource.Success -> {
                    hideKeyboard()
                    binding.etMessage.setText("")
                }

                else -> {}
            }
        }
    }

    private fun setUpPlayerList(){

        playAdapter = PlayAdapter( args.player.id!!,this, args.room.name!!)

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

    override fun vote(id: String, currentPlayer: PlayerInGame, roomName: String) {
        viewModel.votePlayer(currentPlayer.avatar!!,roomName,id)
    }

    override fun unVote(player: PlayerInGame, roomName: String) {
        TODO("Not yet implemented")
    }

    override fun changeVote(player: PlayerInGame, roomName: String) {
        TODO("Not yet implemented")
    }
}