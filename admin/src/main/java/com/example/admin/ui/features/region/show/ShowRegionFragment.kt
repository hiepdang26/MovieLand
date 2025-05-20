package com.example.admin.ui.features.region.show

import RegionAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.data.firebase.model.region.FirestoreRegion
import com.example.admin.databinding.FragmentShowRegionBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.district.show.ShowDistrictFragment
import com.example.admin.ui.features.mainmovie.add.AddRawMovieFragment
import com.example.admin.ui.features.mainmovie.edit.EditMovieFragment
import com.example.admin.ui.features.region.add.AddRegionFragment
import com.example.admin.ui.features.region.edit.EditRegionFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowRegionFragment : BaseFragment<FragmentShowRegionBinding>() {

    private val viewModel: ShowRegionViewModel by viewModels()
    private lateinit var adapter: RegionAdapter

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentShowRegionBinding {
        return FragmentShowRegionBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialData()
        setupClickView()
        setupObserver()
    }

    override fun setupInitialData() {
        adapter = RegionAdapter()
        binding.rcvRegion.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvRegion.adapter = adapter
    }

    override fun setupObserver() {
        lifecycleScope.launch {
            viewModel.regions.collectLatest { regions ->
                adapter.submitList(regions)
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                error?.let {
                    Toast.makeText(requireContext(), "Lỗi: $it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun setupClickView() {
        binding.btnAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, AddRegionFragment()).addToBackStack(null)
                .commit()
        }

        adapter.onItemClick = { region ->
            showRegionOptionsDialog(region)
        }
    }

    private fun showRegionOptionsDialog(region: FirestoreRegion) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_region_options, null)
        dialog.setContentView(view)

        val tvViewDistricts = view.findViewById<TextView>(R.id.tvViewDistricts)
        val tvViewDetails = view.findViewById<TextView>(R.id.tvViewDetails)

        tvViewDistricts.setOnClickListener {
            dialog.dismiss()
            navigateToDistricts(region.id, region.name)
        }

        tvViewDetails.setOnClickListener {
            dialog.dismiss()
            navigateToEditRegion(region.id)
        }

        dialog.show()
    }

    private fun navigateToDistricts(regionId: String, regionName: String) {
        val fragment = ShowDistrictFragment().apply {
            arguments = Bundle().apply {
                putString("regionId", regionId)
                putString("regionName", regionName)

            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToEditRegion(regionId: String) {
        val fragment = EditRegionFragment().apply {
            arguments = Bundle().apply {
                putString("regionId", regionId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }
}
