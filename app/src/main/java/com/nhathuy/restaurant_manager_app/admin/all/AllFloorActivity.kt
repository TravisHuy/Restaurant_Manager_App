package com.nhathuy.restaurant_manager_app.admin.all

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.FloorAdapter
import com.nhathuy.restaurant_manager_app.databinding.ActivityAllFloorBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject
/**
 * Activity to display list of floors.
 *
 * @return 0.1
 * @since 17-02-2025
 * @author TravisHuy
 */
class AllFloorActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAllFloorBinding
    private lateinit var adapter:FloorAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FloorViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllFloorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.getAllFloors()

    }

    private fun setupRecyclerView() {
        adapter = FloorAdapter { floor ->
            Toast.makeText(this, "Selected: ${floor.name}", Toast.LENGTH_SHORT).show()
            binding.circleRecyclerView.toggleVisibility()
        }
        binding.circleRecyclerView.adapter = adapter
        binding.btnFloors.setCircleRecyclerView(binding.circleRecyclerView)
    }
    private fun setupListeners(){
        binding.btnFloors.post {
            val initialCenterX = binding.btnFloors.x + binding.btnFloors.width / 2
            val initialCenterY = binding.btnFloors.y + binding.btnFloors.height / 2
            binding.circleRecyclerView.setCenterPosition(initialCenterX, initialCenterY)
        }

        binding.btnFloors.setOnClickListener {
            binding.circleRecyclerView.toggleVisibility()
            val centerX = binding.btnFloors.x + binding.btnFloors.width / 2
            val centerY = binding.btnFloors.y + binding.btnFloors.height / 2
            binding.circleRecyclerView.setCenterPosition(centerX, centerY)
        }
    }
    private fun observeViewModel() {
        viewModel.floors.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
//                    binding.progressBar.visibility = View.GONE
                    resource.data?.let {
                        floors -> adapter.updateFloors(floors)
                    }
                }
                is Resource.Error -> {
//                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}