package com.nhathuy.restaurant_manager_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.databinding.ActivityRegisterBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSignUp.setOnClickListener {
                val email = textfieldEmail.text.toString()
                val name = textfieldName.text.toString()
                val phoneNumber = textfieldPhonenumber.text.toString()
                val password = textfieldPassword.text.toString()
                val address = textfieldAddress.text.toString()

                if(validateInput(email, password,name,phoneNumber,address)){
                    viewModel.register(name, email, password, phoneNumber, address)
                }
                textLogin.setOnClickListener {
                    startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                }
            }
            btnSignGoogle.setOnClickListener {

            }
            btnSignGithub.setOnClickListener {

            }
        }
    }
    private fun observeViewModel() {
        viewModel.registerResult.observe(this) {
                result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this,LoginActivity::class.java))
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Error ->{
                    Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility= View.VISIBLE
                }
            }

        }
        viewModel.oauthResult.observe(this) {
                result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Error ->{
                    Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility= View.VISIBLE
                }
            }

        }
    }

    private fun validateInput(email:String,password:String,name:String,phoneNumber:String, address:String):Boolean{
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "Please enter a valid email"
            isValid = false
        }
        else{
            binding.textInputLayoutEmail.error = null
        }

        if(password.isEmpty() || password.length < 8){
            binding.textInputLayoutPassword.error = "Password must be least 8 characters"
            isValid=false
        }
        else{
            binding.textInputLayoutPassword.error = null
        }

        if(name.isEmpty()){
            binding.textInputLayoutName.error = "Name not empty"
            isValid = false
        }
        else{
            binding.textInputLayoutName.error = null
        }

        if(phoneNumber.isEmpty() || phoneNumber.length != 10){
            binding.textInputLayoutPhonenumber.error = "Phone number not empty and must 10 numbers"
            isValid = false
        }
        else{
            binding.textInputLayoutPhonenumber.error = null
        }

        if(address.isEmpty()){
            binding.textInputLayoutAddress.error = "Address not empty"
            isValid = false
        }
        else{
            binding.textInputLayoutAddress.error = null
        }
        return isValid
    }
}