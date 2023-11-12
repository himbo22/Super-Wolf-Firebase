package com.example.superwolffirebase.views.mainscreen.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.superwolffirebase.databinding.ActivityLoginBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.viewmodel.activityviewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var intentToRegisterActivity: Intent
    private lateinit var intentToMainScreenActivity: Intent
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentToRegisterActivity = Intent(this@LoginActivity, RegisterActivity::class.java)
        intentToMainScreenActivity = Intent(this@LoginActivity, MainScreenActivity::class.java)

        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.createAccount.setOnClickListener {
            startActivity(intentToRegisterActivity)
            finish()
        }

        viewModel.loginResponse.observe(this) {event->
            if(!event.hasBeenHandled){
                event.getContentIfNotHandled()?.let { resource->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.rlLoading.show()
                        }
                        is Resource.Success -> {
                            binding.rlLoading.invisible()
                            Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_SHORT).show()
                            startActivity(intentToMainScreenActivity)
                            finish()
                        }

                        is Resource.Error -> {
                            binding.rlLoading.invisible()
                            Toast.makeText(this@LoginActivity, "Email or password are invalid!", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

        }
    }
}