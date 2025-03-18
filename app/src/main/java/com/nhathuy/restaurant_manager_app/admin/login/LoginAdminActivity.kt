package com.nhathuy.restaurant_manager_app.admin.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.ActivityLoginAdminBinding
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class LoginAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginAdminBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}