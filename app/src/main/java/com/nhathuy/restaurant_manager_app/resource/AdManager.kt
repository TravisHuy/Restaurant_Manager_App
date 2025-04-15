package com.nhathuy.restaurant_manager_app.resource

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nhathuy.restaurant_manager_app.R

/**
 * AdManager
 *
 * This singleton class is responsible for managing ads in the application
 *
 * @version 0.1
 * @author TravisHuy
 * @since 15.4.2025
 */
object AdManager {
    private const val TAG = "AdManager"

    private var floorInterstitialAd: InterstitialAd? = null
    private var tableInterstitialAd: InterstitialAd? = null

    /**
     * Initialize the mobile ads
     */
    fun initialize(context:Context){
        MobileAds.initialize(context)  {
            init ->
            Log.d(TAG, "MobileAds initialization status: $init")
        }
        loadFloorAd(context)
        loadTableAd(context)
    }

    /**
     * Load interstitial ad floor creation
     */
    fun loadFloorAd(context: Context){
        val adRequest = AdRequest.Builder().build()


        InterstitialAd.load(context, context.getString(R.string.Interstitial_AD_UNIT_ID), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Floor interstitial ad failed to load: ${adError.message}")
                floorInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Floor interstitial ad loaded successfully")
                floorInterstitialAd = interstitialAd
                setupFloorAdCallbacks()
            }
        })
    }
    /**
     * Load interstitial ad table creation
     */
    fun loadTableAd(context: Context){
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, context.getString(R.string.Interstitial_AD_UNIT_ID), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Table interstitial ad failed to load: ${adError.message}")
                tableInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Floor interstitial ad loaded successfully")
                tableInterstitialAd = interstitialAd
                setupTableAdCallbacks()
            }
        })
    }

    /**
     * Set up callbacks for floor interstitial ad
     */
    private fun setupFloorAdCallbacks(){
        floorInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Floor ad was dismissed")
                floorInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Floor ad failed to show: ${adError.message}")
                floorInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Floor ad showed successfully")
            }
        }
    }
    /**
     * Set up callbacks for table interstitial ad
     */
    private fun setupTableAdCallbacks(){
        tableInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Table ad was dismissed")
                tableInterstitialAd= null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Table ad failed to show: ${adError.message}")
                tableInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Table ad showed successfully")
            }
        }
    }
    /**
     * Show floor interstitial ad
     */
    fun showFloorAd(activity:Activity, onAdClosed: () -> Unit = {}){
        if(floorInterstitialAd != null){
            floorInterstitialAd?.show(activity)
            floorInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG,"Floor ad was dismissed")
                    floorInterstitialAd = null
                    loadFloorAd(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Floor ad failed to show: ${adError.message}")
                    floorInterstitialAd = null
                    loadFloorAd(activity)
                    onAdClosed()
                }
            }
        }
        else{
            Log.d(TAG, "Floor ad wasn't loaded yet.")
            loadFloorAd(activity)
            onAdClosed()
        }
    }
    /**
     * Show table interstitial ad
     */
    fun showTableAd(activity:Activity, onAdClosed: () -> Unit = {}){
        if(tableInterstitialAd != null){
            tableInterstitialAd?.show(activity)
            tableInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG,"Table ad was dismissed")
                    tableInterstitialAd = null
                    loadFloorAd(activity)
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Table ad failed to show: ${adError.message}")
                    tableInterstitialAd = null
                    loadFloorAd(activity)
                    onAdClosed()
                }
            }
        }
        else{
            Log.d(TAG, "Table ad wasn't loaded yet.")
            loadFloorAd(activity)
            onAdClosed()
        }
    }
}