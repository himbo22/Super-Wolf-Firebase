package com.example.superwolffirebase.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.superwolffirebase.R
import com.example.superwolffirebase.databinding.ActivitySetUpProfileBinding
import com.example.superwolffirebase.other.Resource
import com.example.superwolffirebase.utils.invisible
import com.example.superwolffirebase.utils.show
import com.example.superwolffirebase.viewmodel.SetUpProfileViewModel
import com.example.superwolffirebase.views.mainscreen.MainScreenActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetUpProfileActivity : AppCompatActivity() {
    private val viewModel by viewModels<SetUpProfileViewModel>()

    private lateinit var binding: ActivitySetUpProfileBinding
    private lateinit var intentToMainScreenActivity: Intent
    private lateinit var cameraIntent: ActivityResultLauncher<Intent>

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var uid: String
    private lateinit var name: String
    private lateinit var gender: String
    private lateinit var avatar: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



        getAllStringExtraFromRegisterActivity()
        intentToMainScreenActivity = Intent(this@SetUpProfileActivity, MainScreenActivity::class.java)

        binding.optionGender.setOnItemClickListener { parent, view, position, id ->
            gender = binding.optionGender.text.toString()
        }

        binding.tvEmail.text = email
        binding.tvName.text = name


//        cameraIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
//            val bitmap = result?.data?.extras?.get("data") as Bitmap
//            binding.avatar.setImageBitmap(bitmap)
//        }

        binding.avatar.setOnClickListener {
//            cameraIntent.launch(
//                Intent(
//                    "android.media.action.IMAGE_CAPTURE"
//                )
//            )
            getContent.launch(
                "image/*"
            )
        }



        binding.confirmButton.setOnClickListener {
            if (binding.optionGender.text.isNullOrBlank()) {
                Toast.makeText(
                    this@SetUpProfileActivity,
                    "Please choose your gender!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                viewModel.saveAvatar(avatar, uid, name, gender, email, "None", "None")
            }
        }

        viewModel.uploadProfileStatus.observe(this) { resource ->
            when (resource) {

                is Resource.Loading -> {
                    binding.rlLoading.show()
                }

                is Resource.Success -> {
                    binding.rlLoading.invisible()
                    startActivity(intentToMainScreenActivity)
                    finish()
                }


                is Resource.Error -> {
                    Toast.makeText(this@SetUpProfileActivity, "No", Toast.LENGTH_SHORT).show()
                }


                else -> {}
            }
        }


    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                return@registerForActivityResult
            } else {
                avatar = uri
                binding.avatar.setImageURI(uri)
            }

        }

    override fun onResume() {
        super.onResume()
        val gender = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(applicationContext, R.layout.drop_down_item, gender)
        binding.optionGender.setAdapter(arrayAdapter)
    }

    private fun getAllStringExtraFromRegisterActivity() {
        uid = intent.getStringExtra("uid").toString()
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        name = intent.getStringExtra("name").toString()
    }

}

