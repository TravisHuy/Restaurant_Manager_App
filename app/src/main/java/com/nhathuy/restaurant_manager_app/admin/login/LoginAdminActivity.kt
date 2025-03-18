package com.nhathuy.restaurant_manager_app.admin.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.ActivityLoginAdminBinding

class LoginAdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}