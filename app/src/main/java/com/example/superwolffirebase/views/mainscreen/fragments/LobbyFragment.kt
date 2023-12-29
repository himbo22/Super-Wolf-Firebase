package com.example.superwolffirebase.views.mainscreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.superwolffirebase.adapter.OnItemClickListener
import com.example.superwolffirebase.adapter.RoomAdapter
import com.example.superwolffirebase.databinding.FragmentLobbyBinding
import com.example.superwolffirebase.model.Room
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.showLog
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

    override fun onResume() {
        super.onResume()
        viewModel.getAllRooms()
        viewModel.playMainMenuMusic()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpView() {
        binding.backBtn.setOnClickListener {
            val action = LobbyFragmentDirections.actionPlayFragmentToMainScreenFragment()
            findNavController().navigate(action)
        }
        binding.btAddRoom.setOnClickListener {
            if (binding.etRoomName.text.isNullOrBlank()) {
                return@setOnClickListener
            } else {
                val name = binding.etRoomName.text.toString()
                viewModel.createNewRoom(name, args.player.id!!, args.player.avatar!!, args.player.name!!)
                binding.etRoomName.setText("")

            }
        }
        binding.swipeLayout.setOnRefreshListener {
            viewModel.getAllRooms()
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun setUpRoomAdapter() {
        adapter = RoomAdapter(this)
        binding.roomList.layoutManager = LinearLayoutManager(requireContext())
        binding.roomList.adapter = adapter
    }

    private fun setUpObserveViewModel() {



        viewModel.newRoom.observe(viewLifecycleOwner) { event ->
            if (event.hasBeenHandled.not()) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Loading -> {

                        }

                        is Resource.Success -> {

                            resource.result.let {

                                val action =
                                    LobbyFragmentDirections.actionPlayFragmentToPlayFragment(
                                        it.first,
                                        args.player,
                                        it.second
                                    )
                                findNavController().navigate(action)


                            }


                        }

                        is Resource.Error -> {

                        }

                        else -> {}
                    }
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

        viewModel.joinRoom(
            room.name!!,
            player.id!!,
            player.avatar!!,
            player.name!!,
        )


        viewModel.joinRoomResult.observe(viewLifecycleOwner) { event ->
            if (!event.hasBeenHandled) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.result.let {
                                val action =
                                    LobbyFragmentDirections.actionPlayFragmentToPlayFragment(
                                        room,
                                        args.player,
                                        it
                                    )
                                findNavController().navigate(action)
                            }


                        }

                        else -> {}
                    }
                }
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Handle the back button press in the fragment
                    // You can perform custom logic here
                    // Example: navigate back to the previous fragment or activity

                }
            })
    }

}