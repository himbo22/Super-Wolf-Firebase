package com.example.superwolffirebase.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.superwolffirebase.R
import com.example.superwolffirebase.adapter.ViewPagerAdapter
import com.example.superwolffirebase.databinding.ActivityTutorialBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.viewmodel.LoginViewModel
import com.example.superwolffirebase.viewmodel.TutorialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var imageList: List<Int>
    private lateinit var intentToLoginActivity: Intent
    private val viewModel by viewModels<TutorialViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentToLoginActivity = Intent(this@TutorialActivity, LoginActivity::class.java)

        viewModel.check()
        checkSkip()
        addImageToList()
        handleView()
        setUpViewPager()
    }

    private fun addImageToList() {
        imageList = ArrayList()
        imageList = imageList + R.drawable.ic_launcher_foreground
        imageList = imageList + R.drawable.ic_launcher_foreground
        imageList = imageList + R.drawable.ic_launcher_foreground
    }

    private fun setUpViewPager() {
        adapter = ViewPagerAdapter(imageList)

        binding.viewpager2.adapter = adapter
        binding.viewpager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.indicator.setViewPager(binding.viewpager2)
    }

    private fun handleView() {
        binding.skip.setOnClickListener {
            viewModel.putBoolean()
            startActivity(intentToLoginActivity)
            finish()
        }
    }

    private fun checkSkip() {
        viewModel.checkSkipStatus.observe(this) {
            when (it) {
                is Resource.Success -> {
                    startActivity(intentToLoginActivity)
                    finish()
                }

                else -> {}
            }
        }
    }
}