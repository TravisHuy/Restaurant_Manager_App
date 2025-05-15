package com.nhathuy.restaurant_manager_app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.databinding.ActivityMainBinding
import com.nhathuy.restaurant_manager_app.service.RestaurantFirebaseMessagingService
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager: SessionManager

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == RestaurantFirebaseMessagingService.NOTIFICATION_RECEIVED) {
                    val data = it.getSerializableExtra("data") as? HashMap<String, String>
                    data?.let { notificationData ->
                        handleNotification(notificationData)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        // Check login state
        lifecycleScope.launch {
            sessionManager.isLoggedIn.collect {
                if (!it) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

        setupNavigation()
        registerFcmToken()

        // Register notification receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(
            notificationReceiver,
            IntentFilter(RestaurantFirebaseMessagingService.NOTIFICATION_RECEIVED)
        )

        // Handle notification if app was launched from notification
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_map, R.id.nav_order, R.id.nav_subtotal, R.id.nav_more)
        )

        val bottomNavView: BottomNavigationView = binding.bottomNavView
        bottomNavView.setupWithNavController(navController)
    }

    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                RestaurantFirebaseMessagingService.registerToken(this, token)
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val type = it.getStringExtra("type")
            val relatedId = it.getStringExtra("relatedId")

            if (type != null) {
                val notificationData = HashMap<String, String>().apply {
                    put("type", type)
                    put("related", relatedId ?: "")
                }
                handleNotification(notificationData)
            }
        }
    }

    private fun handleNotification(notificationData: HashMap<String, String>) {
        val type = notificationData["type"] ?: return
        val relatedId = notificationData["related"] ?: ""

        when (type) {
            "TABLE_RESERVED" -> {
                // Navigate to the table management screen
                navController.navigate(R.id.nav_map)
            }
            "ORDER_CREATED" -> {
                // Navigate to orders
                navController.navigate(R.id.nav_order)
            }
            "PAYMENT_COMPLETED" -> {
                // Navigate to subtotal
                navController.navigate(R.id.nav_subtotal)
            }
            // Add more notification types as needed
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}