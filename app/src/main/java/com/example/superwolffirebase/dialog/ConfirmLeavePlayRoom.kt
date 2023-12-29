package com.example.superwolffirebase.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.superwolffirebase.model.Player
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.dialogviewmodel.ConfirmLeaveRoomViewModel
import com.example.superwolffirebase.views.mainscreen.fragments.PlayFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.NameResolver.Args


@AndroidEntryPoint
class ConfirmLeavePlayRoom : DialogFragment() {


    private val viewModel by viewModels<ConfirmLeaveRoomViewModel>()
    private val args by navArgs<ConfirmLeavePlayRoomArgs>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Are you sure to leave room?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.leaveRoom(args.roomName, args.uid)
                viewModel.stopMusic()
                val action =
                    ConfirmLeavePlayRoomDirections.actionConfirmLeavePlayRoomToLobbyFragment(args.player)
                findNavController().navigate(action)
            }
            .create()


}