package com.nhathuy.restaurant_manager_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.TestAdapter
import com.nhathuy.restaurant_manager_app.databinding.ActivityTestBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding

    private lateinit var adapter: TestAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val menuItemViewModel: MenuItemViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)
        setupRecyclerView()
        observerModel()
        menuItemViewModel.getAllMenuItems()
    }

    private fun setupRecyclerView(){
        adapter = TestAdapter()
        val layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        binding.recTest.layoutManager = layoutManager
        binding.recTest.adapter = adapter
    }
    private fun observerModel(){
        lifecycleScope.launch {
            menuItemViewModel.menuItemsState.collect {
                resources ->
                when(resources){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                        resources?.data?.let {
                            menuItems ->
                            adapter.update(menuItems)
                        }
                    }
                    is Resource.Error ->{

                    }
                }
            }
        }
    }
}