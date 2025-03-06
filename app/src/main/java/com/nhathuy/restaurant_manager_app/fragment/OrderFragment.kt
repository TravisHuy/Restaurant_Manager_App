package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.OrderAdapter
import com.nhathuy.restaurant_manager_app.databinding.FragmentOrderBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [OrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @return 0.1
 * @version 14-02-2025
 * @author TravisHuy
 */
class OrderFragment : Fragment() {

    private lateinit var binding : FragmentOrderBinding
    private lateinit var orderAdapter: OrderAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentOrderBinding.inflate(inflater,container,false)

        setupOrderRecyclerView()
        observeViewModel()
        setupListeners()
        orderViewModel.getAllOrders()


        return binding.root
    }
    private fun setupOrderRecyclerView() {
        orderAdapter = OrderAdapter {
            order ->
            Toast.makeText(requireContext(),"${order.id}",Toast.LENGTH_SHORT).show()
        }
        binding.orderRec.layoutManager = LinearLayoutManager(requireContext())
        binding.orderRec.adapter = orderAdapter

    }
    private fun observeViewModel() {
        orderViewModel.orderItems.observe(requireActivity()) {
            result ->
            when(result){
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let {
                            orders ->
                        Log.d("Order","${orders.size}")
                        orderAdapter.updateOrders(orders)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(),result.message,Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{
                    showLoading(true)
                }
            }
        }
    }
    private fun setupListeners(){
        binding.orderSwipeRefresh.setOnRefreshListener {
            orderViewModel.getAllOrders()
        }
    }
    private fun showLoading(isLoading:Boolean){
        binding.orderSwipeRefresh.isRefreshing = if(isLoading) true else false
    }
}