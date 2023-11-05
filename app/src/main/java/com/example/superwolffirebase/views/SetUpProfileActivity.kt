package com.example.superwolffirebase.views

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivitySetUpProfileBinding
import com.example.superwolffirebase.utils.makeToast
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
    private lateinit var avatar: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.unFinishedProfile()

        getAllStringExtraFromRegisterActivity()

        binding.optionGender.setOnItemClickListener { parent, view, position, id ->
            makeToast(this@SetUpProfileActivity, binding.optionGender.text.toString())
        }

        binding.tvEmail.text = email
        binding.tvName.text = name

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

