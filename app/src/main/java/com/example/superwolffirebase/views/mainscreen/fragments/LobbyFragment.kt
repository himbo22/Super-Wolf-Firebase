package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superwolffirebase.adapter.OnItemClickListener
import com.example.superwolffirebase.adapter.RoomAdapter
import com.example.superwolffirebase.databinding.FragmentLobbyBinding
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.viewmodel.fragmentviewmodel.LobbyViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LobbyFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LobbyViewModel>()
    private lateinit var adapter: RoomAdapter
    private val args by navArgs<LobbyFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)


        setUpView()
        setUpRoomAdapter()
        setUpObserveViewModel()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpView() {
        binding.btAddRoom.setOnClickListener {
            if (binding.etRoomName.text.isNullOrBlank()) {
                return@setOnClickListener
            } else {
                val name = binding.etRoomName.text.toString()
                viewModel.createNewRoom(name)
                binding.etRoomName.text = null
            }
        }
    }

    private fun setUpRoomAdapter() {
        adapter = RoomAdapter(args.player, this)
        binding.roomList.layoutManager = LinearLayoutManager(requireContext())
        binding.roomList.adapter = adapter
    }

    private fun setUpObserveViewModel() {

        viewModel.newRoom.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {

                }

                is Resource.Error -> {

                }
            }
        }




        viewModel.allRoom.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.result.let {
                        adapter.setData(it)
                    }
                }

                is Resource.Error -> {
                    resource.exception.let {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    adapter.setData(emptyList())

                }

                else -> {}
            }
        }
    }

    override fun onItemClick(room: Room) {
        val player = args.player
        var newCardinalNumber = (room.amount!! )
        newCardinalNumber += 1
        viewModel.joinRoom(
            room.name!!,
            room.amount!!,
            player.id!!,
            player.avatar!!,
            player.name!!,
            newCardinalNumber,
            ""
        )

        viewModel.joinRoomResult.observe(viewLifecycleOwner){event->
            if (!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let {resource ->
                    when(resource){
                        is Resource.Success -> {
                            val action = LobbyFragmentDirections.actionPlayFragmentToPlayFragment(player, room)
                            findNavController().navigate(action)
                        }

                        else -> {}
                    }
                }
            }
        }

    }


}