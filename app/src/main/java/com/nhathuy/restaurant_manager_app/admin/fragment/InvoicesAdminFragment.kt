package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.InvoiceItemAdapter
import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.data.model.PaymentMethod
import com.nhathuy.restaurant_manager_app.databinding.FragmentInvoicesAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.InvoiceViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class InvoicesAdminFragment : Fragment() {
    private var _binding: FragmentInvoicesAdminBinding? = null
    private val binding get()= _binding!!

    private lateinit var invoiceAdapter: InvoiceItemAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val invoiceView:InvoiceViewModel by viewModels { viewModelFactory }

    private var allInvoices: List<Invoice> = emptyList()

    private var searchJob: Job?  =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInvoicesAdminBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSearchFilter()
        setupPagination()


        loadInvoices()
    }
    private fun setupRecyclerView(){
        invoiceAdapter = InvoiceItemAdapter (
            onViewDetailClick = {

            },
            onPrintClick = {

            }
        )
        binding.rvInvoices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInvoices.adapter = invoiceAdapter
    }
    private fun setupObservers(){
        invoiceView.allInvoice.observe(viewLifecycleOwner){
            result ->
            when(result) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    result.data?.let{ invoices->
                        allInvoices = invoices
                        if(invoices.isEmpty()){
                            showEmptyState()
                        }
                        else{
                            showInvoices(invoices)
                        }
                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    showError(result.message ?: "Unknow error occured")
                }
            }
        }
    }
    private fun setupSearchFilter(){
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(300)
                filterInvoices(text.toString(),getCurrentPaymentMethodFilter())
            }
        }
        binding.chipAll.isChecked = true
        binding.chipGroup.setOnCheckedStateChangeListener {
            _,checkIds ->
            filterInvoices(binding.etSearch.text.toString(),getCurrentPaymentMethodFilter())
        }
    }

    private fun filterInvoices(query: String, paymentMethod: PaymentMethod?) {
        if(allInvoices.isEmpty()) return

        val filteredList = allInvoices.filter { 
            invoice ->
            val matchQuery = query.isEmpty() || invoice.id.contains(query,ignoreCase = true) ||
                    invoice.orderId.contains(query,ignoreCase = true)
            val matchesPaymentMethod= paymentMethod == null || invoice.paymentMethod == paymentMethod

            matchQuery && matchesPaymentMethod
        }
        if(filteredList.isEmpty()){
            showEmptyState()
        }
        else{
            showInvoices(filteredList)
        }
    }

    private fun setupPagination(){

    }
    private fun loadInvoices(){
        invoiceView.getAllInvoice()
    }
    private fun getCurrentPaymentMethodFilter(): PaymentMethod?{
        return when {
            binding.chipCash.isChecked ->PaymentMethod.CASH
            binding.chipPending.isChecked -> PaymentMethod.CARD
            binding.chipOnline.isChecked -> PaymentMethod.ONLINE
            else -> null
        }
    }
    private fun showLoading(){
        binding.progressBar.visibility=View.VISIBLE
        binding.rvInvoices.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }
    private fun hideLoading(){
        binding.progressBar.visibility = View.GONE
    }
    private fun showInvoices(invoices: List<Invoice>){
        binding.rvInvoices.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        invoiceAdapter.submitList(invoices)
    }
    private fun showEmptyState(){
        binding.rvInvoices.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showError(errorMessage:String){
        Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_SHORT).show()
    }

}