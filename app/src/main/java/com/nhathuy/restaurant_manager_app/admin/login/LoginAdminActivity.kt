package com.nhathuy.restaurant_manager_app.admin.login

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.admin.AdminActivity
import com.nhathuy.restaurant_manager_app.admin.register.RegisterAdminActivity
import com.nhathuy.restaurant_manager_app.databinding.ActivityLoginAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import com.nhathuy.restaurant_manager_app.util.Constants
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class LoginAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginAdminBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    private lateinit var sharedPreferences: SharedPreferences
    private var currentEmail:String = ""
    private var currentPassword:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)
        loadSaveCredentials()

        setupListeners()
        observeViewModel()
    }
    private fun loadSaveCredentials(){
        val email = sharedPreferences.getString(Constants.KEY_EMAIL,"")
        val password = sharedPreferences.getString(Constants.KEY_PASSWORD,"")
        val rememberMe = sharedPreferences.getBoolean(Constants.KEY_REMEMBER_ME,false)

        binding.textfieldEmail.setText(email)
        binding.textfieldPassword.setText(password)
        binding.checkBoxRememberMe.isChecked = rememberMe
    }
    private fun setupListeners(){
        binding.tvRegisterAdmin.setOnClickListener {
            startActivity(Intent(this@LoginAdminActivity,RegisterAdminActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            currentEmail = binding.textfieldEmail.text.toString()
            currentPassword = binding.textfieldPassword.text.toString()

            if(validateInput(currentEmail,currentPassword)){
                viewModel.loginAdmin(currentEmail,currentPassword)
            }
        }
    }
    private fun observeViewModel(){
        viewModel.loginAdminResult.observe(this) {
                result ->
            when(result){
                is Resource.Success -> {
                    if(binding.checkBoxRememberMe.isChecked){
                        saveCredentials(currentEmail,currentPassword)
                    }
                    else{
                        clearCredentials()
                    }
                    startActivity(Intent(this, AdminActivity::class.java))
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
    private fun saveCredentials(email: String, password: String) {
        with(sharedPreferences.edit()) {
            putString(Constants.KEY_EMAIL, email)
            putString(Constants.KEY_PASSWORD, password)
            putBoolean(Constants.KEY_REMEMBER_ME, true)
            apply()
        }
    }

    private fun clearCredentials() {
        with(sharedPreferences.edit()) {
            remove(Constants.KEY_EMAIL)
            remove(Constants.KEY_PASSWORD)
            putBoolean(Constants.KEY_REMEMBER_ME, false)
            apply()
        }
    }
    private fun validateInput(email:String,password:String):Boolean{
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
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

        return isValid
    }
}