package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.admin.add.AddFloorActivity
import com.nhathuy.restaurant_manager_app.databinding.FragmentMoreBinding


/**
 * [MoreFragment] subclass.
 * Use the [MoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @return 0.1
 * @version 14-02-2025
 * @author TravisHuy
 */
class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMoreBinding.inflate(inflater, container, false)
        binding.btnAddFloor.setOnClickListener {
            findNavController().navigate(R.id.action_nav_more_to_addFloorActivity)
        }
        return binding.root
    }

}