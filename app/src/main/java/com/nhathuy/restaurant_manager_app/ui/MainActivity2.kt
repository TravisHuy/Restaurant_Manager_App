package com.nhathuy.restaurant_manager_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.admin.add.AddCategoryActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddFloorActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddMenuItemActivity
import com.nhathuy.restaurant_manager_app.admin.all.AllFloorActivity
import com.nhathuy.restaurant_manager_app.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        binding.btnAddFloors.setOnClickListener {
            startActivity(Intent(this, AddFloorActivity::class.java))
        }
        binding.btnAllFloor.setOnClickListener {
            startActivity(Intent(this, AllFloorActivity::class.java))
        }
        binding.btnAddCategory.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
        binding.addMenuItem.setOnClickListener {
            startActivity(Intent(this, AddMenuItemActivity::class.java))
        }
    }
}