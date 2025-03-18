package com.nhathuy.restaurant_manager_app.admin.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.admin.login.LoginAdminActivity
import com.nhathuy.restaurant_manager_app.databinding.ActivityRegisterAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.ui.LoginActivity
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class RegisterAdminActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterAdminBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val authViewModel : AuthViewModel by viewModels  { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupClickListeners()
        observeViewModel()
    }
    private fun setupClickListeners(){
        binding.apply {
            btnSign.setOnClickListener {
                val email = tfEmailAdmin.text.toString()
                val password = tfPasswordAdmin.text.toString()
                val name = tfNameAdmin.text.toString()
                val phoneNumber = tfPhoneNumberAdmin.text.toString()
                val address = tfAddressAdmin.text.toString()

                if(validateInput(email,password,name,phoneNumber,address)){
                    authViewModel.registerAdmin(name,email, password, phoneNumber, address)
                }
            }

            tvLoginAdmin.setOnClickListener {
                startActivity(Intent(this@RegisterAdminActivity,LoginAdminActivity::class.java))
            }
        }
    }
    private fun observeViewModel(){
        authViewModel.registerAdminResult.observe(this){
                result ->
            when(result){
                is Resource.Success -> {
                    startActivity(Intent(this, LoginAdminActivity::class.java))
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
            binding.textInputLayoutNameAdmin.error = "Name not empty"
            isValid = false
        }
        else{
            binding.textInputLayoutNameAdmin.error = null
        }

        if(phoneNumber.isEmpty() || phoneNumber.length != 10){
            binding.textInputLayoutPhoneNumberAdmin.error = "Phone number not empty and must 10 numbers"
            isValid = false
        }
        else{
            binding.textInputLayoutPhoneNumberAdmin.error = null
        }

        if(address.isEmpty()){
            binding.textInputLayoutAddressAdmin.error = "Address not empty"
            isValid = false
        }
        else{
            binding.textInputLayoutAddressAdmin.error = null
        }
        return isValid
    }
}