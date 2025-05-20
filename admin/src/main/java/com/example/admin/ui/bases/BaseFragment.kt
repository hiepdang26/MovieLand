package com.example.admin.ui.bases
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    var _binding: VB? = null
    protected val binding get() = _binding ?: throw IllegalArgumentException("Binding is only available between onCreateView and onDestroyView")


    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun setupInitialData()
    abstract fun setupObserver()
    abstract fun setupClickView()

}