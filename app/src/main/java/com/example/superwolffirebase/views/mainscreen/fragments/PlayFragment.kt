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
import com.bumptech.glide.Glide
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
import com.example.superwolffirebase.utils.hide
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
    private var isClick = false


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

        binding.healPotion.setOnClickListener {
            isClick = if (isClick) {
                viewModel.witchSave(args.room.name!!, true)
                true
            } else {
                viewModel.witchSave(args.room.name!!, false)
                false
            }
        }

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
        viewModel.getHealPower(args.room.name!!)
        viewModel.getRoomHarmPower(args.room.name!!)
        viewModel.getRoomGameStarted(args.room.name!!)
        viewModel.getRoomWitchPhase(args.room.name!!)
        viewModel.getRoomGameEnded(args.room.name!!)
        viewModel.getRoomIsDayStatus(args.room.name!!)


        viewModel.seerPick.observe(viewLifecycleOwner){resource->
            when(resource){
                is Resource.Success -> {
                    resource.result.let {
                        binding.notifyPlayerSeerPicked.show()
                        binding.tvPlayerSeerPicked.text = it.name
                        Glide.with(binding.imgPlayerSeerPicked.context).load(it.avatar)
                            .into(binding.imgPlayerSeerPicked)
                        when (it.role) {
                            WEREWOLF -> {
                                binding.imgPlayerRoleSeerPicked.setImageResource(R.drawable.werewolf_icon)
                            }
                            VILLAGER -> {
                                binding.imgPlayerRoleSeerPicked.setImageResource(R.drawable.villager)
                            }
                            WITCH -> {
                                binding.imgPlayerRoleSeerPicked.setImageResource(R.drawable.witch)
                            }
                            else -> {
                                binding.imgPlayerRoleSeerPicked.setImageResource(R.drawable.shield)
                            }
                        }
                    }

                }

                else -> {}
            }
        }

        viewModel.roomIsDayStatus.observe(viewLifecycleOwner){event->
            if (!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let { resource->
                    when (resource) {
                        is Resource.Success -> {
                            resource.result.let {
                                playAdapter.setIsDay(it)
                            }
                        }

                        is Resource.Error -> {
                            showLog(resource.exception.message.toString())
                        }

                        else -> {}
                    } }
            }
        }
        viewModel.witchSaveStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (resource.result) {
                        binding.playerBeingTargeted.setTextColor(R.color.green)
                    } else {
                        binding.playerBeingTargeted.setTextColor(R.color.red)
                    }
                }

                is Resource.Error -> {
                    showLog(resource.exception.message.toString())
                }

                else -> {}
            }
        }

        viewModel.playerBeingTargeted.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.result.let { player ->
                        binding.notification.show()
                        if (player == PlayerInGame()) { // no one being targeted
                            binding.playerBeingTargeted.text = "${player.name} being targeted"
                            binding.playerBeingTargeted.show()
                            binding.healPotion.show()
                        } else {
                            binding.playerBeingTargeted.text = "No one being targeted"
                            binding.playerBeingTargeted.show()
                            binding.healPotion.hide()
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

        viewModel.witchPhaseStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    playAdapter.setWitchPhase(resource.result)
                }

                is Resource.Error -> {
                    showLog(resource.exception.message.toString())
                }

                else -> {}
            }
        }

        viewModel.gameEndedStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    playAdapter.setGameEnded(resource.result)
                }

                is Resource.Error -> {
                    showLog(resource.exception.message.toString())
                }

                else -> {}
            }
        }

        viewModel.gameStartedStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    playAdapter.setGameStarted(resource.result)
                }

                is Resource.Error -> {
                    showLog(resource.exception.message.toString())
                }

                else -> {}
            }
        }

        viewModel.harmPowerStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    playAdapter.setHarmPower(resource.result)
                }

                is Resource.Error -> {
                    showLog(resource.exception.message.toString())
                }

                else -> {}
            }
        }



        viewModel.startNewDay.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), resource.result, Toast.LENGTH_SHORT).show()
                    binding.notifyPlayerSeerPicked.invisible()
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
                            binding.ready.isClickable = resource.result.gameStarted != true
                            binding.days.text = resource.result.days.toString()
                            binding.nights.text = resource.result.nights.toString()
                        }

                        is Resource.Error -> {
                            showLog(resource.exception.message.toString())
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

        playAdapter = PlayAdapter(args.player.id!!, this)

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
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Handle the back button press in the fragment
                    // You can perform custom logic here

                    // Example: navigate back to the previous fragment or activity
                    requireActivity().supportFragmentManager.popBackStack()
                    viewModel.leaveRoomWhenClickBackButton(args.room.name!!, args.player.id!!)
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
            args.player.id!!,
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
        viewModel.seerPick(currentPlayer)
    }

    override fun werewolfVote(currentPlayer: PlayerInGame) {
        viewModel.votePlayer(
            currentPlayer.avatar!!,
            args.room.name!!,
            args.player.id!!,
            currentPlayer.id!!
        )
    }

    override fun werewolfUnVote(currentPlayer: PlayerInGame) {
        viewModel.unVotePlayer(args.room.name!!, args.player.id!!, currentPlayer.id!!)
    }

    override fun werewolfChangeVote(currentPlayer: PlayerInGame) {
        viewModel.changeVotePlayer(
            args.room.name!!,
            args.player.id!!,
            currentPlayer.id!!,
            currentPlayer.avatar!!
        )
    }

    override fun guardVote(currentPlayer: PlayerInGame) {
        viewModel.guardPick(args.room.name!!, currentPlayer, args.player.id!!)
    }

    override fun guardUnVote(currentPlayer: PlayerInGame) {
        viewModel.guardUnPick(args.room.name!!, currentPlayer, args.player.id!!)
    }

    override fun guardChangeVote(currentPlayer: PlayerInGame) {
        viewModel.guardChangePick(args.room.name!!, args.player.id!!, currentPlayer)
    }


    override fun witchPick(currentPlayer: PlayerInGame) {
        viewModel.witchKill(args.room.name!!, currentPlayer, args.player.id!!)
    }

    override fun witchUnPick(currentPlayer: PlayerInGame) {
        viewModel.witchUnKill(args.room.name!!, currentPlayer, args.player.id!!)

    }

    override fun witchChangePick(currentPlayer: PlayerInGame) {
        viewModel.witchChangeKill(args.room.name!!, currentPlayer, args.player.id!!)
    }


}