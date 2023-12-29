package com.example.superwolffirebase.views.mainscreen.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.superwolffirebase.databinding.ActivityMainScreenBinding
import com.example.superwolffirebase.utils.showLog
import com.example.superwolffirebase.viewmodel.activityviewmodel.MainScreenActivityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    private val viewModel by viewModels<MainScreenActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeMusic()
    }

    override fun onStop() {
        super.onStop()
        viewModel.pauseMusic()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMusic()
    }



}