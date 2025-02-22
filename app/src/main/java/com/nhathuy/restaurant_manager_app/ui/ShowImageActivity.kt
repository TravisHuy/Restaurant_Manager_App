package com.nhathuy.restaurant_manager_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.ActivityShowImageBinding
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class ShowImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowImageBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val menuItemViewModel: MenuItemViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}