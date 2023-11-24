package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.ExperimentalTime


@GlideModule
@AndroidEntryPoint
class PlayFragment : Fragment(), OnItemClick {
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<PlayFragmentArgs>()
    private val viewModel by viewModels<PlayViewModel>()
    private lateinit var playAdapter: PlayAdapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var playerInGame: PlayerInGame
    private lateinit var timer: CountDownTimer


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

    private fun setUpView() {

        binding.ready.setOnClickListener {
            viewModel.ready(args.room.name!!, args.player.id!!)
        }

        binding.getback.setOnClickListener {
            viewModel.leaveRoom(args.room.name!!, args.room.amount!!, args.player.id!!)
        }


        binding.send.setOnClickListener {
            if (binding.etMessage.text.isNullOrBlank()) {
                return@setOnClickListener
            } else {
                val message = binding.etMessage.text.toString()
                viewModel.sendMessage(args.room.name!!, message, args.player.name!!)
            }
        }




    }


    private fun setUpViewModel() {
        viewModel.getAllPlayers(args.room.name!!, args.player.id!!)
        viewModel.getAllMessages(args.room.name!!)
        viewModel.getPlayerInGame(args.player.id!!, args.room.name!!)
        viewModel.getRoom(args.room.name!!)
        viewModel.prepareToStartGame(args.room.name!!)




        viewModel.prepareToStartGame.observe(viewLifecycleOwner){resource->

            when(resource){
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "yes sir", Toast.LENGTH_SHORT).show()
                    viewModel.startGame(args.room.name!!)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(),"resource.exception.message", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }

        viewModel.readyStatus.observe(viewLifecycleOwner){resource->
            when(resource){
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Waiting for other people!!", Toast.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.exception.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }



        viewModel.timeCountDown.observe(viewLifecycleOwner){resource->
            when (resource) {
                is Resource.Success -> {
                    binding.timer.text = resource.result
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.exception.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }


        viewModel.getRoom.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource){
                        is Resource.Success -> {
                            playAdapter.setRoom(resource.result)
                            binding.ready.isClickable = resource.result.gameStarted != true
                            binding.days.text = resource.result.days.toString()
                            binding.nights.text = resource.result.nights.toString()
                        }

                        is Resource.Error -> {

                        }

                        else -> {}
                    }
                }
            }
        }

        viewModel.votePlayer.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {

                        }

                        else -> {}
                    }
                }
            }
        }


        viewModel.getPlayerInGame.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.result.let {
//                                if (it.role == ""){
//                                    binding.skill.invisible()
//                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

        viewModel.allMessage.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
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
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {

                        is Resource.Success -> {
                            val action =
                                PlayFragmentDirections.actionPlayFragmentToLobbyFragment(args.player)
                            findNavController().navigate(action)
                        }

                        else -> {}
                    }

                }
            }
        }

        viewModel.allPlayer.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
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
        viewModel.sendMessResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideKeyboard()
                    binding.etMessage.setText("")
                }

                else -> {}
            }
        }
    }

    private fun setUpPlayerList() {

        playAdapter = PlayAdapter(args.playerInGame.id!!, this)

        binding.playerList.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
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

    override fun vote(currentPlayer: PlayerInGame) {
        viewModel.votePlayer(
            currentPlayer.avatar!!,
            args.room.name!!,
            args.playerInGame.id!!,
            currentPlayer.id!!
        )
    }

    override fun unVote(player: PlayerInGame, roomName: String) {
        TODO("Not yet implemented")
    }

    override fun changeVote(player: PlayerInGame, roomName: String) {
        TODO("Not yet implemented")
    }
}