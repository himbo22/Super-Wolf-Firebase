package com.example.superwolffirebase.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivitySetUpProfileBinding
import com.example.superwolffirebase.viewmodel.SetUpProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetUpProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetUpProfileBinding
    private val viewModel by viewModels<SetUpProfileViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}