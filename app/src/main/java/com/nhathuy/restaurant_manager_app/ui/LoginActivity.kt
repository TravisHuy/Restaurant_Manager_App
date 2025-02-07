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
import com.nhathuy.restaurant_manager_app.databinding.ActivityLoginBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AuthViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = textfieldEmail.editText?.text.toString()
                val password = textfieldPassword.editText?.text.toString()

                if(validateInput(email,password)){
                    viewModel.login(email, password)
                }
            }
            textView2.setOnClickListener {
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            }
            btnSignGoogle.setOnClickListener {

            }
            binding.btnSignGithub.setOnClickListener {

            }
        }
    }
    private fun observeViewModel() {
        viewModel.loginResult.observe(this) {
            result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this,MainActivity::class.java))
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Error ->{
                    Toast.makeText(this,result.message,Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility=View.VISIBLE
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
                    Toast.makeText(this,result.message,Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility= View.GONE
                }
                is Resource.Loading ->{
                    binding.progressBar.visibility=View.VISIBLE
                }
            }

        }
    }

    private fun validateInput(email:String,password:String):Boolean{
        var isValid = true

        if (email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textfieldEmail.error = "Please enter a valid email"
            isValid = false
        }
        else{
            binding.textfieldEmail.error = null
        }

        if(password.isEmpty() || password.length < 8){
            binding.textfieldPassword.error = "Password must be least 8 characters"
            isValid=false
        }
        else{
            binding.textfieldPassword.error = null
        }

        return isValid
    }
}