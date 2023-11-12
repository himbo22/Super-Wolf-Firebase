package com.example.superwolffirebase.views.mainscreen.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.superwolffirebase.databinding.ActivityMainScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}