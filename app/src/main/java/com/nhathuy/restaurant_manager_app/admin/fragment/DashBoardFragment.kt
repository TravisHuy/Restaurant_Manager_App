package com.nhathuy.restaurant_manager_app.admin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.FragmentDashBoardBinding

/**
 * A DashBoard [Fragment] subclass.
 *
 */
class DashBoardFragment : Fragment() {

    private lateinit var binding : FragmentDashBoardBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDashBoardBinding.inflate(LayoutInflater.from(context),container,false)



        return binding.root
    }

}