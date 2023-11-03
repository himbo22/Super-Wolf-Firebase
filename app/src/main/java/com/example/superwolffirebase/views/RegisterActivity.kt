package com.example.superwolffirebase.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivityRegisterBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this@RegisterActivity, "Unacceptable blank!!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                viewModel.register(name, email, password)
            }
        }

        viewModel.registerResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this@RegisterActivity, "Ok", Toast.LENGTH_SHORT).show()

                }

                is Resource.Error -> {

                    Toast.makeText(
                        this@RegisterActivity,
                        "Check the information again!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }
}