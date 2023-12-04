package com.example.superwolffirebase.views.mainscreen.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.annotation.GlideModule
import com.example.superwolffirebase.R
import com.example.superwolffirebase.adapter.MessageAdapter
import com.example.superwolffirebase.adapter.PlayAdapter
import com.example.superwolffirebase.databinding.FragmentPlayBinding
import com.example.superwolffirebase.model.PlayerInGame
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Constant.GUARD
import com.example.superwolffirebase.other.Constant.SEER
import com.example.superwolffirebase.other.Constant.VILLAGER
import com.example.superwolffirebase.other.Constant.WEREWOLF
import com.example.superwolffirebase.other.Constant.WITCH
import com.example.superwolffirebase.other.OnItemClick
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.hideKeyboard
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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

        binding.leave.setOnClickListener {
            viewModel.leaveRoom(args.room.name!!, args.player.id!!)
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


    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun setUpViewModel() {
        viewModel.getAllPlayers(args.room.name!!, args.player.id!!)
        viewModel.getAllMessages(args.room.name!!)
        viewModel.getPlayerInGame(args.player.id!!, args.room.name!!)
        viewModel.getRoom(args.room.name!!)
        viewModel.prepareToStartGame(args.room.name!!)
        viewModel.playersRemaining(args.room.name!!)
        viewModel.startGame(args.room.name!!, args.player.id!!)
        viewModel.autoPickRole(args.room.name!!, args.player.id!!)
        viewModel.playerCreateRoom(args.room.name!!, args.room.playerCreateRoomId!!)




        viewModel.playerBeingTargeted.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.result.let { player ->
                        if (player == PlayerInGame()) { // no one being targeted
                            binding.notification.show()
                            binding.playerBeingTargeted.text = "${player.name} being targeted"
                        } else {
                            binding.notification.show()
                            binding.playerBeingTargeted.text = "No one being targeted"
                        }
                    }
                }

                is Resource.Error -> {
                    binding.notification.invisible()
                }

                else -> {}
            }
        }

        viewModel.healPowerStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (resource.result) {
                        binding.healPotion.show()
                        binding.noMoreHeal.invisible()
                    } else {
                        binding.healPotion.invisible()
                        binding.noMoreHeal.show()
                    }
                }


                else -> {
                    binding.notification.show()

                }
            }
        }

        viewModel.harmPowerStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {

                }


                else -> {}
            }
        }

//        viewModel.startGame.observe(viewLifecycleOwner){resource->
//            when(resource){
//                is Resource.Success -> {
//                    viewModel.startNewDay(args.room.name!!)
//                }
//
//                else -> {}
//            }
//        }

        viewModel.startNewDay.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), resource.result, Toast.LENGTH_SHORT).show()
//                    binding.days.setTextColor(R.color.black)
//                    binding.nights.setTextColor(R.color.black)
//                    binding.timer.setTextColor(R.color.black)
//                    binding.getBack.setColorFilter(R.color.black)
//                    binding.menu.setColorFilter(R.color.black)
//                    binding.screen.setBackgroundColor(R.color.white)
                }

                else -> {}
            }
        }

        viewModel.startNewNight.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), resource.result, Toast.LENGTH_SHORT).show()
//                    binding.days.setTextColor(R.color.white)
//                    binding.nights.setTextColor(R.color.white)
//                    binding.timer.setTextColor(R.color.white)
//                    binding.getBack.setColorFilter(R.color.white)
//                    binding.menu.setColorFilter(R.color.white)
//                    binding.screen.setBackgroundColor(R.color.night)
                }

                else -> {}
            }
        }



        viewModel.role.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "You are ${resource.result}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }






        viewModel.timeCountDown.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.timer.text = resource.result
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
        }




        viewModel.getRoom.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
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

        viewModel.unVotePlayer.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {

                }

                is Resource.Error -> {

                }

                else -> {}
            }

        }




        viewModel.getPlayerInGame.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.result.let {
                        when (it.role) {
                            VILLAGER -> {
                                binding.skill.show()
                                binding.skill.setImageResource(R.drawable.villager)
                            }

                            WEREWOLF -> {
                                binding.skill.show()
                                binding.skill.setImageResource(R.drawable.werewolf_icon)
                            }

                            SEER -> {
                                binding.skill.show()
                                binding.skill.setImageResource(R.drawable.seer)
                            }

                            WITCH -> {
                                binding.skill.show()
                                binding.skill.setImageResource(R.drawable.witch)
                            }

                            GUARD -> {
                                binding.skill.show()
                                binding.skill.setImageResource(R.drawable.shield)
                            }

                            else -> {
                                binding.skill.invisible()
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    showLog(resource.exception.message!!)
                }

                else -> {}

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


    // Handle on event click back button in android device
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.leaveRoom(args.room.name!!, args.player.id!!)
            }

        })
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

    override fun unVote(currentPlayer: PlayerInGame) {
        viewModel.unVotePlayer(
            args.room.name!!,
            args.player.id!!,
            currentPlayer.id!!
        )
    }

    override fun changeVote(currentPlayer: PlayerInGame) {
        viewModel.changeVotePlayer(
            args.room.name!!,
            args.player.id!!,
            currentPlayer.id!!,
            currentPlayer.avatar!!
        )
    }

    override fun seerPick(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }

    override fun werewolfVote(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }

    override fun werewolfUnVote(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }

    override fun werewolfChangeVote(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }

    override fun guardVote(currentPlayer: PlayerInGame) {
        showLog("hoang cac to")
        viewModel.guardPick(args.room.name!!, currentPlayer, args.player.id!!)
    }

    override fun guardUnVote(currentPlayer: PlayerInGame) {
        viewModel.guardUnPick(args.room.name!!, currentPlayer, args.player.id!!)
    }

    override fun guardChangeVote(currentPlayer: PlayerInGame) {
        viewModel.guardChangePick(args.room.name!!, args.player.id!!, currentPlayer)
    }

    override fun witchPhase() {
        viewModel.getHarmPower(args.room.name!!)
        viewModel.getHealPower(args.room.name!!)


    }

    override fun witchPick(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }

    override fun witchUnPick(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }

    override fun witchChangePick(currentPlayer: PlayerInGame) {
        TODO("Not yet implemented")
    }


}