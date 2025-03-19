package com.nhathuy.restaurant_manager_app.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.databinding.ActivityAdminBinding
/**
 * Activity for all item with admin.
 * This screen allows activity the user used to the restaurant.
 *
 * @return 0.1
 * @since 17-03-2025
 * @author TravisHuy
 */
class AdminActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAdminBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,binding.appBarMain.toolbar,
        R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()
        binding.navView.setNavigationItemSelectedListener {
            menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}