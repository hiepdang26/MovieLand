package com.example.movieland

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.admin.ui.bases.BaseActivity
import com.example.movieland.databinding.ActivityMainBinding
import com.example.movieland.ui.features.auth.signin.SignInActivity
import com.example.movieland.ui.features.home.movie.show.ShowMovieFragment
import com.example.movieland.ui.features.personal.main.ShowPersonalFragment
import com.example.movieland.ui.features.voucher.show.ShowVoucherFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), ShowPersonalFragment.LogoutCallback {
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

        chipNavigationBar.setItemSelected(R.id.navigation_home, true)
        openFragment(ShowMovieFragment())
    }

    private fun setupBottomNavigation() {
        chipNavigationBar.setOnItemSelectedListener { itemId ->
            when (itemId) {
                R.id.navigation_voucher -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowVoucherFragment) {
                        openFragment(ShowVoucherFragment())
                    }
                    true
                }

                R.id.navigation_home -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowMovieFragment) {
                        openFragment(ShowMovieFragment())
                    }
                    true
                }

                R.id.navigation_personal -> {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) !is ShowPersonalFragment) {
                        openFragment(ShowPersonalFragment())
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
    override fun onLogoutSuccess() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // Khởi động SignInActivity và finish MainActivity
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}