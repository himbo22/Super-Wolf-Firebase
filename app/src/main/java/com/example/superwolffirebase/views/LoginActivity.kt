package com.example.superwolffirebase.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivityLoginBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var intentToRegisterActivity: Intent
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentToRegisterActivity = Intent(this@LoginActivity, RegisterActivity::class.java)

        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.createAccount.setOnClickListener {
            startActivity(intentToRegisterActivity)
        }

        viewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_SHORT).show()
                }

                is Resource.Error -> {
                    Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
}