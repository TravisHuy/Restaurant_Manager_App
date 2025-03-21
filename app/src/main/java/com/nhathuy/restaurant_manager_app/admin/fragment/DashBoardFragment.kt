package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.TableAdapter
import com.nhathuy.restaurant_manager_app.adapter.TableAdminAdapter
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.data.model.Reservation
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.databinding.FragmentDashBoardBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ReservationViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * A DashBoard [Fragment] subclass.
 *
 * @return 0.1
 * @since 20-03-2025
 * @author TravisHuy
 */
class DashBoardFragment : Fragment() {

    private lateinit var binding : FragmentDashBoardBinding
    private lateinit var tableAdapter : TableAdminAdapter
    private lateinit var floorAdapter: ArrayAdapter<String>
    private var floorId : String? = null
    private val reservationCache = mutableMapOf<String, Reservation>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val reservationViewModel: ReservationViewModel by viewModels { viewModelFactory }
    private val floorViewModel:FloorViewModel by viewModels { viewModelFactory  }
    private val tableViewModel:TableViewModel by viewModels { viewModelFactory  }
    private val orderViewModel : OrderViewModel by viewModels { viewModelFactory  }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDashBoardBinding.inflate(LayoutInflater.from(context),container,false)

        setupTableRecyclerView()
        setupFloorRecyclerView()
        observerViewModel()

        floorViewModel.getAllFloors()
        orderViewModel.getAllOrders()

        return binding.root
    }
    // Inside DashBoardFragment class
    private fun setupTableRecyclerView() {
        tableAdapter = TableAdminAdapter(
            listOf(),
            { reservationId -> reservationCache[reservationId] },
            { table ->
                // Only fetch reservation info if there is a reservationId
                if (!table.reservationId.isNullOrEmpty()) {
                    getInformationReservation(table.reservationId)
                    Log.d("DashBoardFragment", "Table selected, reservationId: ${table.reservationId}")
                } else {
                    Log.d("DashBoardFragment", "Table selected, no reservation")
                    // Maybe show a message that this table has no reservation
                    Toast.makeText(requireContext(), "This table has no reservation", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recTable.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recTable.adapter = tableAdapter
    }

    /**
     * Returns a cached reservation if available, otherwise returns null
     * The adapter will use this to quickly access reservation data
     */
    private fun getReservationFromId(reservationId: String): Reservation? {
        return reservationCache[reservationId]
    }

    private fun getInformationReservation(reservationId:String) {
        fetchReservationById(reservationId)
    }
    private fun fetchReservationById(reservationId: String) {
        if (reservationId.isEmpty()) return

        reservationViewModel.getReservation(reservationId)
        viewLifecycleOwner.lifecycleScope.launch {
            reservationViewModel.getReservationResult.collect { resource ->
                try {
                    when(resource) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            resource.data?.let { reservation ->
                                // Cache the reservation
                                reservationCache[reservationId] = reservation
                                // Notify adapter to refresh views
                                tableAdapter.notifyDataSetChanged()
                            }
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            val errorMsg = resource.message ?: "An error occurred"
                            showError(errorMsg)
                            Log.e("DashBoardFragment", "Error getting reservation: $errorMsg")
                        }
                        else -> {
                            showLoading(false)
                        }
                    }
                }
                catch (e: Exception) {
                    if (e is CancellationException) {
                        // Coroutine was cancelled normally, don't show error
                        Log.d("DashBoardFragment", "Get reservation operation was cancelled")
                    } else {
                        showLoading(false)
                        showError("Error: ${e.message}")
                        Log.e("DashBoardFragment", "Exception during get reservation: ${e.message}", e)
                    }
                }
            }
        }
    }
    private fun setupFloorRecyclerView(){
        floorAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFloors.adapter = floorAdapter

        binding.spinnerFloors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFloor = parent?.getItemAtPosition(position).toString()
                val floor = floorViewModel.floors.value?.data?.find {
                    it.name == selectedFloor
                }
                floor?.let {
                    floorId = it.id
                    showListTable(it.id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observerViewModel(){
        floorViewModel.floors.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { floors ->
                        val floorNames = floors.map { it.name }
                        floorAdapter.clear()
                        floorAdapter.addAll(floorNames)

                        if (floors.isNotEmpty()) {
                            // First item will be selected automatically
                            floorId = floors[0].id
                            showListTable(floors[0].id)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load floors")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        floorViewModel.floorById.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { floor ->
                        tableViewModel.getTablesByFloorId(floor.id)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load floor details")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        tableViewModel.tablesByFloor.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { tables ->
                        tableAdapter.updateTables(tables)
                        loadReservationsForTables(tables)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load tables")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        //
        orderViewModel.orderItems.observe(viewLifecycleOwner) {
            result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { orders ->
                       showInformation(orders)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load tables")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showInformation(orders: List<Order>) {
        binding.tvTotalOrder.text = orders.size.toString()

        val totalSales = orders.sumOf { it.totalAmount }
        binding.tvTotalSales.text = formatCurrency(totalSales)

        val avgOrderValue = if(orders.isNotEmpty()) {
            totalSales / orders.size
        }
        else{
            0.0
        }

        binding.tvAvgOrderValue.text = formatCurrency(avgOrderValue)

        // Setup sales over time chart
        setupSalesOverTimeChart(orders)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSalesOverTimeChart(orders: List<Order>) {
        val chart = binding.chartSales

        val salesByDate=  orders
            .sortedBy { it.orderTime }
            .groupBy { it.orderTime }
            .map { (date, ordersOnDate) ->
                Pair(date, ordersOnDate.sumOf { it.totalAmount })
            }
        val entries = ArrayList<Entry>()
        salesByDate.forEachIndexed { index, (_,amount) ->  entries.add(Entry(index.toFloat(),amount.toFloat()))}

        val dataSet = LineDataSet(entries,"Sales")
        dataSet.color = ContextCompat.getColor(requireContext(),R.color.grey_300)
        dataSet.valueTextColor  = ContextCompat.getColor(requireContext(),R.color.black)
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(),R.color.gray_dark))
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 10f

        val lineData = LineData(dataSet)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(salesByDate.map {
            it.first.format(DateTimeFormatter.ofPattern("MM/dd"))
        })
        xAxis.labelRotationAngle = -45f

        val leftAxis = chart.axisLeft
        leftAxis.setDrawZeroLine(true)

        chart.axisRight.isEnabled = false

        chart.data = lineData
        chart.animateX(1000)
        chart.invalidate()
    }
    private fun formatCurrency(amount:Double) :String {
        return "$" + String.format("%.2f", amount)
    }
    // Add this function to DashBoardFragment
    private fun loadReservationsForTables(tables: List<Table>) {
        // Clear old cache when loading new tables
        reservationCache.clear()

        // Only fetch reservations for tables that have a reservation ID
        tables.forEach { table ->
            if (!table.reservationId.isNullOrEmpty()) {
                fetchReservationById(table.reservationId)
            }
        }
    }

    private fun showListTable(floorId:String){
        floorViewModel.getFloorById(floorId)
    }


    private fun showLoading(isLoading: Boolean) {
//        binding.swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}