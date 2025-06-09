package com.example.admin.ui.features.combo.show

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.R
import com.example.admin.databinding.FragmentShowCombosBinding
import com.example.admin.ui.bases.BaseFragment
import com.example.admin.ui.features.combo.add.AddCombosFragment
import com.example.admin.ui.features.region.add.AddRegionFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShowCombosFragment : BaseFragment<FragmentShowCombosBinding>() {

    private val viewModel: ShowCombosViewModel by viewModels()
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var comboAdapter: ComboAdapter
    private var districtMap: Map<String, String> = emptyMap()
    private var selectedDistrictId: String? = null
    private var selectedDistrictName: String? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentShowCombosBinding {
        return FragmentShowCombosBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObserver()
        setupClickView()
        viewModel.loadDistricts()
    }

    private fun setupRecyclerView() {
        binding.rcvDistrict.layoutManager = LinearLayoutManager(requireContext())
        comboAdapter = ComboAdapter(emptyList())
        binding.rcvDistrict.adapter = comboAdapter
    }

    override fun setupObserver() {
        // Quan sát danh sách quận
        lifecycleScope.launch {
            viewModel.districts.collectLatest { districts ->
                Log.d("ShowCombosFragment", "✅ Số lượng districts: ${districts.size}")
                districts.forEach {
                    Log.d("ShowCombosFragment", "➡️ District: id=${it.id}, name=${it.name}")
                }

                if (districts.isEmpty()) {
                    Toast.makeText(requireContext(), "⚠️ Không có district nào", Toast.LENGTH_SHORT).show()
                }

                districtMap = districts.associate { it.name to it.id }
                val districtNames = districts.map { it.name }

                spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    districtNames
                )
                binding.spinnerDistrict.adapter = spinnerAdapter

                Log.d("ShowCombosFragment", "🎯 Spinner set với ${districtNames.size} items")
            }
        }

        // Combo
        lifecycleScope.launch {
            viewModel.combos.collectLatest { combos ->
                Log.d("ShowCombosFragment", "✅ Combo count: ${combos.size}")
                comboAdapter = ComboAdapter(combos)
                binding.rcvDistrict.adapter = comboAdapter
            }
        }

        // Lỗi
        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMsg ->
                errorMsg?.let {
                    Log.e("ShowCombosFragment", "❌ ERROR: $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }



    override fun setupClickView() {
        binding.spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val districtName = spinnerAdapter.getItem(position)
                val districtId = districtMap[districtName]

                districtId?.let {
                    selectedDistrictId = it
                    selectedDistrictName = districtName
                    viewModel.loadCombosByDistrict(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnAdd.setOnClickListener {
            val addFragment = AddCombosFragment().apply {
                arguments = Bundle().apply {
                    putString("districtId", selectedDistrictId)
                    putString("districtName", selectedDistrictName)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, addFragment)
                .addToBackStack(null)
                .commit()
        }
    }


    override fun setupInitialData() {
    }
}
