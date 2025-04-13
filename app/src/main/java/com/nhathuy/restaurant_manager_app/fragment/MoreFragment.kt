package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.admin.add.AddFloorActivity
import com.nhathuy.restaurant_manager_app.admin.login.LoginAdminActivity
import com.nhathuy.restaurant_manager_app.databinding.FragmentMoreBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.ui.LoginActivity
import com.nhathuy.restaurant_manager_app.ui.TestActivity
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject


/**
 * [MoreFragment] subclass.
 * create an instance of this fragment.
 *
 * @return 0.1
 * @version 14-02-2025
 * @author TravisHuy
 */
class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val authViewModel: AuthViewModel by viewModels { viewModelFactory }

    //
    private var navigatingToAdmin = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(inflater, container, false)

        observeViewModel()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.linearLogout.setOnClickListener {
            authViewModel.logout()
        }
        binding.linearLayoutAdmin.setOnClickListener {
            navigateAdminDialog()
        }
        binding.supportFragment.setOnClickListener {
            startActivity(Intent(requireActivity(),TestActivity::class.java))
        }
    }
    private fun observeViewModel(){
        authViewModel.logoutResult.observe(viewLifecycleOwner) {
            result ->
            when(result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)

                    if (navigatingToAdmin) {
                        val intent = Intent(requireContext(), LoginAdminActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                    navigatingToAdmin = false
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Logout issue: ${result.message}, redirecting to login", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    navigatingToAdmin = false
                }
            }
        }
    }
    private fun showLoading(show:Boolean){
        if(show) {
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.progressBar.visibility = View.GONE
        }
    }
    private fun navigateAdminDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout first when visit admin UI")
            .setMessage("Logout now")
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { dialog, which ->
                navigatingToAdmin = true
                authViewModel.logout()
            }
            .show()
    }
}