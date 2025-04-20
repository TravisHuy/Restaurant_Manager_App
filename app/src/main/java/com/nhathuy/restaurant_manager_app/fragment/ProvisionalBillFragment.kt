package com.nhathuy.restaurant_manager_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.FragmentProvisionalBillBinding

/**
 * Fragment responsible for displaying a provisional bill to customer
 * Used to provide a quick overview of the total cost before final checkout.
 *
 * @return 0.1
 * @version 20-04-2025
 * @author TravisHuy
 */

class ProvisionalBillFragment : Fragment() {
    private var _binding : FragmentProvisionalBillBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_provisional_bill, container, false)
    }


}