package com.nhathuy.restaurant_manager_app.admin.add

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.model.Floor
import com.nhathuy.restaurant_manager_app.databinding.ActivityAddFloorBinding
import com.nhathuy.restaurant_manager_app.resource.AdManager
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject
/**
 * Activity for adding a new floor to the restaurant.
 * This screen allows the user to add a new floor to the restaurant.
 *
 * @return 0.1
 * @since 15-02-2025
 * @author TravisHuy
 */
class AddFloorActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddFloorBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FloorViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFloorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        adManager.loadInterstitialAd(this)

        setupClickListeners()
        observeViewModel()
    }
    private fun setupClickListeners(){
        binding.btnAddFloor.setOnClickListener {
            val floorName = binding.edAddFloorName.text.toString()
            if(validateInput(floorName)){
                viewModel.addFloor(Floor("",floorName, emptyList()))
            }
        }
    }
    private fun observeViewModel(){
        viewModel.addFloorResult.observe(this){
            result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    clearInput()
                    setResult(Activity.RESULT_OK)
                    adManager.showInterstitialAd(this){
                        finish()
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this,result.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun validateInput(floorName: String): Boolean {
        if (floorName.isEmpty()) {
            binding.layoutAddFloorName.error = "Floor name is required"
            return false
        }
        return true
    }
    private fun clearInput(){
        binding.edAddFloorName.text?.clear()
    }
}