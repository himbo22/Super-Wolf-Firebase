package com.example.superwolffirebase.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivitySetUpProfileBinding
import com.example.superwolffirebase.viewmodel.SetUpProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetUpProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetUpProfileBinding
    private val viewModel by viewModels<SetUpProfileViewModel>()
    private lateinit var genderList: Array<String>
    private lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        genderList = arrayOf("Male", "Female", "Others")
        adapter = ArrayAdapter(applicationContext, R.layout.drop_down_item, R.id.gender, genderList)

        binding.optionGender.setAdapter(adapter)

        binding.optionGender.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this@SetUpProfileActivity, binding.optionGender.text, Toast.LENGTH_SHORT).show()
        }

    }
}