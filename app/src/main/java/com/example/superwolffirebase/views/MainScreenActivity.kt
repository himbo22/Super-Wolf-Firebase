package com.example.superwolffirebase.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivityMainScreenBinding
import com.example.superwolffirebase.databinding.ActivitySetUpProfileBinding
import com.example.superwolffirebase.viewmodel.MainScreenViewModel
import com.example.superwolffirebase.viewmodel.SetUpProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    private val viewModel by viewModels<MainScreenViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}