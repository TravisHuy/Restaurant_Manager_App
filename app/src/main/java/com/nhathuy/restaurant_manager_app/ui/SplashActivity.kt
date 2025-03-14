package com.nhathuy.restaurant_manager_app.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.databinding.ActivitySplashBinding
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var currentProgress =0
    private val maxProgress =100
    private val progressUpdateInterval = 50L

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupProgressBar()
        startProgressAnimation()
    }

    private fun setupProgressBar() {
        binding.progressBar.apply {
            setMax(maxProgress)
            setProgress(0)
            setStartColor(Color.parseColor("#0D47A1"))
            setEndColor(Color.parseColor("#2196F3"))
            setBackgroundColor(Color.parseColor("#E3F2FD"))
            setCornerRadius(4f)


            val gradientColors = intArrayOf(
                Color.parseColor("#0D47A1"),
                Color.parseColor("#1976D2"),
                Color.parseColor("#2196F3")
            )
            setGradientColors(gradientColors)
        }
    }
    private fun startProgressAnimation() {
        val handler = Handler(Looper.getMainLooper())
        val progressIncrement = maxProgress * progressUpdateInterval / 1500

        val runnable = object : Runnable {
            override fun run() {
                if (currentProgress < maxProgress) {
                    currentProgress += progressIncrement.toInt()
                    binding.progressBar.setProgress(currentProgress)
                    handler.postDelayed(this, progressUpdateInterval)
                } else {

                    sessionManager.checkAuthState()
                    val intent = if(sessionManager.isLoggedIn.value){
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }
                    else{
                        Intent(this@SplashActivity, LoginActivity::class.java)
                    }
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                }
            }
        }

        handler.post(runnable)
    }
}