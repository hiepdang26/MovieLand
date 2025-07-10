package com.example.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.admin.databinding.ActivityMainBinding
import com.example.admin.ui.bases.BaseActivity
import com.example.admin.ui.features.apimovie.ApiMovieFragment
import com.example.admin.ui.features.combo.show.ShowCombosFragment
import com.example.admin.ui.features.mainmovie.show.ShowMovieFragment
import com.example.admin.ui.features.region.show.ShowRegionFragment
import com.example.admin.ui.features.room.choosedistrict.ChooseDistrictFragment
import com.example.admin.ui.features.showtimes.choosedistrictandroom.district.ChooseDistrictForRoomFragment
import com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.district.ChooseDistrictForTicketFragment
import com.example.admin.ui.features.voucher.show.ShowVoucherFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var chipNavigationBar: ChipNavigationBar

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = getViewBinding()
        setContentView(binding.root)

        chipNavigationBar = findViewById<ChipNavigationBar>(R.id.bottom_nav_menu)
        setupBottomNavigation()
        chipNavigationBar.setItemSelected(R.id.navigation_movie, true)
        openFragment(ShowMovieFragment())
    }

    private fun setupBottomNavigation() {
        chipNavigationBar.setOnItemSelectedListener { itemId ->
            when (itemId) {
                R.id.navigation_api -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowVoucherFragment) {
                        openFragment(ShowVoucherFragment())
                    }
                    true
                }

                R.id.navigation_movie -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowMovieFragment) {
                        openFragment(ShowMovieFragment())
                    }
                    true
                }

                R.id.navigation_region -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowRegionFragment) {
                        openFragment(ShowRegionFragment())
                    }
                    true
                }

                R.id.navigation_room -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ChooseDistrictFragment) {
                        openFragment(ChooseDistrictFragment())
                    }
                    true

                }

                R.id.navigation_showtime -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ChooseDistrictForRoomFragment) {
                        openFragment(ChooseDistrictForRoomFragment())
                    }
                    true
                }

                R.id.navigation_ticket -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ChooseDistrictForTicketFragment) {
                        openFragment(ChooseDistrictForTicketFragment())
                    }
                    true
                }
                R.id.navigation_popcorn -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowCombosFragment) {
                        openFragment(ShowCombosFragment())
                    }
                    true
                }


                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val tag = fragment::class.java.name
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentContainerView, fragment, tag).commit()
    }


    fun hideNavigationBar() {
        val height = binding.bottomNavMenu.height.toFloat()
        binding.bottomNavMenu.animate().translationY(height).duration = 400
    }

    fun showNavigationBar() {
        binding.bottomNavMenu.animate().translationY(0f).duration = 400
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}