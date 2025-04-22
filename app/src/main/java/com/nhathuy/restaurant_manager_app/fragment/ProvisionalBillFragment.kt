package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.adapter.OrderMenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.databinding.FragmentProvisionalBillBinding
import com.nhathuy.restaurant_manager_app.viewmodel.InvoiceViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.PaymentViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * Fragment responsible for displaying a provisional bill to customer
 * Used to provide a quick overview of the total cost before final checkout.
 *
 * @return 0.1
 * @version 20-04-2025
 * @author TravisHuy
 */

class ProvisionalBillFragment : DialogFragment() {
    private var _binding : FragmentProvisionalBillBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val paymentViewModel: PaymentViewModel by viewModels { viewModelFactory }
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }
    private val orderItemViewModel : OrderItemViewModel by viewModels { viewModelFactory }
    private val invoiceViewModel : InvoiceViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: OrderMenuItemAdapter
    private var orderItems = mutableListOf<OrderItemDTO>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            dialog ->
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width,height)
            dialog.window?.setWindowAnimations(R.style.DialogAnimation)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProvisionalBillBinding.inflate(inflater,container,false)
        return binding.root
    }


}