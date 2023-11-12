package com.example.superwolffirebase.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.superwolffirebase.viewmodel.dialogviewmodel.ConfirmLogoutViewModel
import com.example.superwolffirebase.views.mainscreen.activities.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmLogOutDialog : DialogFragment() {

    private val viewModel by viewModels<ConfirmLogoutViewModel>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Are you sure you want to logout?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes"){ _,_ ->
                viewModel.logOut()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            .create()
}