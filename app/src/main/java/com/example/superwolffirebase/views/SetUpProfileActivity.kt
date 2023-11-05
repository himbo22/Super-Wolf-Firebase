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
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var uid: String
    private lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllStringExtraFromRegisterActivity()


        binding.optionGender.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this@SetUpProfileActivity, binding.optionGender.text, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        val gender = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_item, gender)
        binding.optionGender.setAdapter(arrayAdapter)
    }

    private fun getAllStringExtraFromRegisterActivity(){
        uid = intent.getStringExtra("uid").toString()
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        name = intent.getStringExtra("name").toString()
    }
}

