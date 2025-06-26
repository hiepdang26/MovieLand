package com.example.movieland.ui.features.home.combo

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.ui.bases.BaseFragment
import com.example.movieland.R
import com.example.movieland.data.firebase.model.ticket.FirestoreTicket
import com.example.movieland.databinding.FragmentChooseComboBinding
import com.example.movieland.ui.features.home.payment.PaymentFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseComboFragment : BaseFragment<FragmentChooseComboBinding>() {
    private var selectedSeatCount: Int = 0
    private var totalPrice: Double = 0.0
    private var districtId: String = ""
    private var showtimeId: String = ""
    private var selectedTickets: ArrayList<FirestoreTicket>? = null

    private val viewModel: ChooseComboViewModel by viewModels()
    private lateinit var adapter: ComboAdapter
    private var isProceedToPayment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentChooseComboBinding {
        return FragmentChooseComboBinding.inflate(inflater, container, false)
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
        setupObserver()
        setupClickView()
    }

    override fun setupInitialData() {
        arguments?.let {
            selectedSeatCount = it.getInt("selectedSeatCount", 0)
            totalPrice = it.getDouble("totalPrice", 0.0)
            districtId = it.getString("districtId", "")
            showtimeId = it.getString("showtimeId", "")
            selectedTickets = it.getParcelableArrayList("selectedTickets")

        }
        binding.rcvCombo.layoutManager = LinearLayoutManager(requireContext())

        adapter = ComboAdapter(emptyList(), onIncrease = { combo ->
            viewModel.increaseCount(combo.id)
            updateTotalPrice()
        }, onDecrease = { combo ->
            viewModel.decreaseCount(combo.id)
            updateTotalPrice()
        })

        binding.rcvCombo.adapter = adapter

        viewModel.loadCombos(districtId)

        binding.txtTotalPrice.text = "Tổng tiền: ${formatPrice(totalPrice)} đ"
    }

    override fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.comboList.collect { combos ->
                adapter.submitList(combos)
                updateTotalPrice()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.comboCounts.collect { counts ->
                adapter.updateCounts(counts)
                updateTotalPrice()
            }
        }
    }

    override fun setupClickView() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBack()
                }
            })

        binding.btnBack.setOnClickListener {
            handleBack()
        }
        binding.btnPay.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có chắc chắn muốn thanh toán không?")
                .setPositiveButton("Đồng ý") { dialog, _ ->
                    dialog.dismiss()
                    isProceedToPayment = true
                    val selectedCombos = getSelectedCombos()

                    val bundle = Bundle().apply {
                        putInt("selectedSeatCount", selectedSeatCount)
                        putString("showtimeId", showtimeId)
                        putDouble("totalPrice", calculateTotalPrice())
                        putParcelableArrayList("selectedTickets", selectedTickets)
                        putParcelableArrayList("selectedCombos", selectedCombos)
                    }
                    Log.d("PaymentFragment", "selectedCombos: $selectedCombos")

                    val paymentFragment = PaymentFragment()
                    paymentFragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, paymentFragment).addToBackStack(null)
                        .commit()
                }.setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun calculateTotalPrice(): Double {
        val combos = viewModel.comboList.value
        val counts = viewModel.comboCounts.value

        val totalComboPrice = combos.sumOf { combo ->
            val count = counts[combo.id] ?: 0
            combo.price * count
        }
        return totalPrice + totalComboPrice
    }

    private fun formatPrice(price: Double): String {
        return String.format("%,.0f", price)
    }

    private fun updateTotalPrice() {
        val combos = viewModel.comboList.value
        val counts = viewModel.comboCounts.value

        val totalComboPrice = combos.sumOf { combo ->
            val count = counts[combo.id] ?: 0
            combo.price * count
        }

        val total = totalPrice + totalComboPrice

        binding.txtTotalPrice.text = "${formatPrice(total)} đ"
    }

    private fun getSelectedCombos(): ArrayList<ComboSelected> {
        val combos = viewModel.comboList.value
        val counts = viewModel.comboCounts.value

        val selected = ArrayList<ComboSelected>()
        combos.forEach { combo ->
            val count = counts[combo.id] ?: 0
            if (count > 0) {
                selected.add(
                    ComboSelected(
                        comboId = combo.id,
                        comboName = combo.name,
                        comboPrice = combo.price,
                        comboImageUrl = combo.imageUrl,
                        quantity = count
                    )
                )
            }
        }
        return selected
    }
    override fun onResume() {
        super.onResume()
        isProceedToPayment = false
    }
    private fun handleBack() {
        if (!isProceedToPayment) {
            selectedTickets?.forEach { ticket ->
                viewModel.resetTicketIfLockedByCurrentUser(
                    showtimeId = showtimeId,
                    ticketId = ticket.ticketId,
                    userId = FirebaseAuth.getInstance().currentUser?.uid
                )
            }
        }
        parentFragmentManager.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isProceedToPayment) {
            selectedTickets?.forEach { ticket ->
                viewModel.resetTicketIfLockedByCurrentUser(
                    showtimeId = showtimeId,
                    ticketId = ticket.ticketId,
                    userId = FirebaseAuth.getInstance().currentUser?.uid
                )
            }
        }
    }
}