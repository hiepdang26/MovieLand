package com.example.admin.ui.features.ticket.choosedistricandroomandshowtime.showtime

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.R
import com.example.admin.databinding.FragmentChooseDistrictForTicketBinding
import com.example.admin.databinding.FragmentChooseShowtimeForTicketBinding
import com.example.admin.ui.bases.BaseFragment

class ChooseShowtimeForTicketFragment :  BaseFragment<FragmentChooseShowtimeForTicketBinding>() {



    private val viewModel: ChooseShowtimeForTicketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentChooseShowtimeForTicketBinding {
        return FragmentChooseShowtimeForTicketBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =  getViewBinding(inflater, container)
        return binding.root
    }

    override fun setupInitialData() {
        TODO("Not yet implemented")
    }

    override fun setupObserver() {
        TODO("Not yet implemented")
    }

    override fun setupClickView() {
        TODO("Not yet implemented")
    }
}