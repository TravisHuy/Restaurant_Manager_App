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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
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
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
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

    private enum class TimeRange{
        DAY, WEEK, MONTH, YEAR
    }
    private var currentTimeRange = TimeRange.DAY
    private var allOrders = listOf<Order>()

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

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
        setupTimeRangeChips()
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupTimeRangeChips() {
        // Đặt chip Day được chọn mặc định
        binding.chipDay.isChecked = true

        binding.timeRangeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            currentTimeRange = when (checkedId) {
                R.id.chipDay -> TimeRange.DAY
                R.id.chipWeek -> TimeRange.WEEK
                R.id.chipMonth -> TimeRange.MONTH
                R.id.chipYear -> TimeRange.YEAR
                else -> TimeRange.DAY
            }

            updateChartWithTimeRange()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateChartWithTimeRange() {
        if (allOrders.isNotEmpty()) {
            setupSalesOverTimeChart(allOrders)
        }
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
                        allOrders = orders
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

        if (orders.isEmpty()) {
            chart.setNoDataText("No sales data available")
            chart.invalidate()
            return
        }
        val salesByTimeRange = getSalesByTimeRange(orders)

        val entries = ArrayList<Entry>()
        salesByTimeRange.forEachIndexed { index, (_, amount) ->
            entries.add(Entry(index.toFloat(), amount.toFloat()))
        }

        if (entries.isEmpty()) {
            chart.setNoDataText("No sales data available for selected time range")
            chart.invalidate()
            return
        }


        val dataSet = LineDataSet(entries, "Sales")
        configureChartDataSet(dataSet)

        val lineData = LineData(dataSet)

        configureChart(chart, lineData, salesByTimeRange.map { it.first })
    }
    private fun configureChartDataSet(dataSet: LineDataSet) {
        dataSet.apply {
            color = ContextCompat.getColor(requireContext(), R.color.grey_300)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.black)
            lineWidth = 2f
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.gray_dark))
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER

            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.grey_300)
        }
    }

    private fun configureChart(chart: LineChart, lineData: LineData, labels: List<String>) {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            legend.textSize = 12f
            setTouchEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -45f
                labelCount = kotlin.math.min(7, labels.size)
                granularity = 1f
            }

            axisLeft.apply {
                setDrawZeroLine(true)
                textSize = 12f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "$" + value.toInt().toString()
                    }
                }
            }

            axisRight.isEnabled = false
            data = lineData
            animateX(1000)
            invalidate()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSalesByTimeRange(orders: List<Order>): List<Pair<String, Double>> {
        return when (currentTimeRange) {
            TimeRange.DAY -> getDailySales(orders)
            TimeRange.WEEK -> getWeeklySales(orders)
            TimeRange.MONTH -> getMonthlySales(orders)
            TimeRange.YEAR -> getYearlySales(orders)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDailySales(orders: List<Order>): List<Pair<String, Double>> {
        // Create a map to track the processed days
        val salesByDay = mutableMapOf<String, Double>()

        // Get the date range from the first order to the last order
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Filter orders with valid dates and sort by time
        val validOrders = orders.mapNotNull { order ->
            try {
                val dateTime = LocalDateTime.parse(order.orderTime, dateTimeFormatter)
                dateTime to order
            } catch (e: Exception) {
                Log.e("DashBoardFragment", "Error parsing date: ${order.orderTime}", e)
                null
            }
        }.sortedBy { it.first }

        if (validOrders.isEmpty()) return emptyList()

        // Get the first and last date
        val firstDate = validOrders.first().first.toLocalDate()
        val lastDate = validOrders.last().first.toLocalDate()

        // Create a list of dates from firstDate to lastDate
        var currentDate = firstDate
        while (!currentDate.isAfter(lastDate)) {
            salesByDay[currentDate.format(dateFormat)] = 0.0
            currentDate = currentDate.plusDays(1)
        }

        // Calculate total sales for each day
        validOrders.forEach { (dateTime, order) ->
            val dateStr = dateTime.format(dateFormat)
            salesByDay[dateStr] = salesByDay.getOrDefault(dateStr, 0.0) + order.totalAmount
        }

        // Switch to list and sort by date
        return salesByDay.entries.map { (date, amount) ->
            Pair(date, amount)
        }.sortedBy { it.first }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeeklySales(orders: List<Order>): List<Pair<String, Double>> {
        // Same behavior as getDailySales but group by week
        val salesByWeek = mutableMapOf<String, Double>()

        val validOrders = orders.mapNotNull { order ->
            try {
                val dateTime = LocalDateTime.parse(order.orderTime, dateTimeFormatter)
                dateTime to order
            } catch (e: Exception) {
                Log.e("DashBoardFragment", "Error parsing date: ${order.orderTime}", e)
                null
            }
        }.sortedBy { it.first }

        if (validOrders.isEmpty()) return emptyList()

        // Group by week
        validOrders.forEach { (dateTime, order) ->
            val weekOfYear = dateTime.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
            val year = dateTime.year
            val weekLabel = "W$weekOfYear-$year"

            salesByWeek[weekLabel] = salesByWeek.getOrDefault(weekLabel, 0.0) + order.totalAmount
        }

        // Sort by week
        return salesByWeek.entries
            .map { (week, amount) -> Pair(week, amount) }
            .sortedBy { it.first }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthlySales(orders: List<Order>): List<Pair<String, Double>> {
        val salesByMonth = mutableMapOf<String, Double>()
        val monthFormat = DateTimeFormatter.ofPattern("MMM yyyy")

        val validOrders = orders.mapNotNull { order ->
            try {
                val dateTime = LocalDateTime.parse(order.orderTime, dateTimeFormatter)
                dateTime to order
            } catch (e: Exception) {
                Log.e("DashBoardFragment", "Error parsing date: ${order.orderTime}", e)
                null
            }
        }.sortedBy { it.first }

        if (validOrders.isEmpty()) return emptyList()

        // Get the first and last month
        val firstMonth = YearMonth.from(validOrders.first().first)
        val lastMonth = YearMonth.from(validOrders.last().first)

        // Create a list of months from firstMonth to lastMonth
        var currentMonth = firstMonth
        while (!currentMonth.isAfter(lastMonth)) {
            val monthLabel = currentMonth.atDay(1).format(monthFormat)
            salesByMonth[monthLabel] = 0.0
            currentMonth = currentMonth.plusMonths(1)
        }

        // Calculate total sales for each month
        validOrders.forEach { (dateTime, order) ->
            val monthLabel = dateTime.format(monthFormat)
            salesByMonth[monthLabel] = salesByMonth.getOrDefault(monthLabel, 0.0) + order.totalAmount
        }

        // Switch to list and sort by month
        return salesByMonth.entries
            .map { (month, amount) -> Pair(month, amount) }
            .sortedBy {
                // Sort by actual time, not by sequence
                YearMonth.parse(it.first, monthFormat)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getYearlySales(orders: List<Order>): List<Pair<String, Double>> {
        val salesByYear = mutableMapOf<String, Double>()

        val validOrders = orders.mapNotNull { order ->
            try {
                val dateTime = LocalDateTime.parse(order.orderTime, dateTimeFormatter)
                dateTime to order
            } catch (e: Exception) {
                Log.e("DashBoardFragment", "Error parsing date: ${order.orderTime}", e)
                null
            }
        }.sortedBy { it.first }

        if (validOrders.isEmpty()) return emptyList()

        // Take the first and last year
        val firstYear = validOrders.first().first.year
        val lastYear = validOrders.last().first.year

        // Generate a list of years from firstYear to lastYear
        for (year in firstYear..lastYear) {
            salesByYear[year.toString()] = 0.0
        }

        // Calculate total sales for each year
        validOrders.forEach { (dateTime, order) ->
            val yearStr = dateTime.year.toString()
            salesByYear[yearStr] = salesByYear.getOrDefault(yearStr, 0.0) + order.totalAmount
        }

        // Switch to list and sort by year
        return salesByYear.entries
            .map { (year, amount) -> Pair(year, amount) }
            .sortedBy { it.first }
    }

    private fun formatCurrency(amount: Double): String {
        return "$" + String.format("%.2f", amount)
    }
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