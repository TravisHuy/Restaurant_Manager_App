package com.nhathuy.restaurant_manager_app.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.admin.fragment.DashBoardFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.InvoicesAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.MenuItemsAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.OrderAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.SettingAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.StaffManagementAdminFragment
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

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, DashBoardFragment())
                .commit()
            binding.navView.setCheckedItem(R.id.nav_dashboard)
        }
        binding.navView.setNavigationItemSelectedListener {
            menuItem ->
            when(menuItem.itemId) {
                R.id.nav_dashboard -> {
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,DashBoardFragment())
                        .commit()
                }
                R.id.nav_menu_items-> {
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,MenuItemsAdminFragment())
                        .commit()
                }
                R.id.nav_order-> {
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,
                        OrderAdminFragment()
                    )
                        .commit()
                }
                R.id.nav_invoices -> {
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,InvoicesAdminFragment()
                    ).commit()
                }
                R.id.nav_staff_management -> {
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,StaffManagementAdminFragment())
                        .commit()
                }
                R.id.nav_setting -> {
                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,SettingAdminFragment())
                        .commit()
                }
            }
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