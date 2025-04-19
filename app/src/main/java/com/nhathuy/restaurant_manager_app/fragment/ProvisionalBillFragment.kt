package com.nhathuy.restaurant_manager_app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhathuy.restaurant_manager_app.R



/**
 * A simple [Fragment] subclass.
 * Use the [ProvisionalBillFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProvisionalBillFragment : Fragment() {


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