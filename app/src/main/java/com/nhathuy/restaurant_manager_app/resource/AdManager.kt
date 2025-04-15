package com.nhathuy.restaurant_manager_app.resource

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nhathuy.restaurant_manager_app.R
import javax.inject.Inject

/**
 * AdManager
 *
 * This singleton class is responsible for managing ads in the application
 *
 * @version 0.1
 * @author TravisHuy
 * @since 15.4.2025
 */
class AdManager @Inject constructor(){

    private var interstitialAd: InterstitialAd? =null
    private var isAdLoading = false
    private var adCounter =  0
    private val AD_FREQUENCY = 2

    /**
     * Load interstitial ad
     */
    fun loadInterstitialAd(context: Context){
        if(interstitialAd != null || isAdLoading) return

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context,context.getString(R.string.Interstitial_AD_UNIT_ID),adRequest,
            object: InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isAdLoading = false
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    isAdLoading = false
                    interstitialAd = ad
                    setupAdCallbacks()
                }
            })
    }
    /**
     * Set up callbacks for ad
     */
    private fun setupAdCallbacks(){
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }
        }
    }
    /**
     * Decide whether to show ads based on ferquency
     * Return true if ads should be shown
     */
    fun shouldShowAd() : Boolean {
        adCounter++
        return adCounter % AD_FREQUENCY == 0 && interstitialAd !=null
    }
    /**
     * Show interstitial ad
     */
    fun showInterstitialAd(activity: Activity,onAdClosed: () -> Unit = {}){
        if(interstitialAd != null){
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdClosed()
                    loadInterstitialAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                    onAdClosed()
                    loadInterstitialAd(activity)
                }
            }
            interstitialAd?.show(activity)
        }
        else{
            onAdClosed()
            loadInterstitialAd(activity)
        }
    }

    /**
     * Display ads in a UX-friendly way
     */
    fun showInterstitialAdWithUX(activity: Activity, onAdClosed: () -> Unit = {}){
        if(!shouldShowAd()){
            onAdClosed()
            loadInterstitialAd(activity)
            return
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle("Quick Ad")
            .setMessage("We'll show you a quick advertisement. Thank you for supporting you app")
            .setPositiveButton("Ok") { _,_ ->
                if(interstitialAd != null){
                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            onAdClosed()
                            loadInterstitialAd(activity)
                        }

                        override fun onAdShowedFullScreenContent() {
                            interstitialAd = null
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        interstitialAd?.show(activity)
                    }, 1000)
                }
                else {
                    onAdClosed()
                    loadInterstitialAd(activity)
                }
            }
            .setNegativeButton("Skip") { _, _ ->
                onAdClosed()
            }
            .create()

        dialog.show()
    }
}