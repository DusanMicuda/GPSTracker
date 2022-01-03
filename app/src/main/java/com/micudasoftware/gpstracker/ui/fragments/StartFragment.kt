package com.micudasoftware.gpstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.databinding.FragmentStartBinding
import com.micudasoftware.gpstracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(layoutInflater, container, false)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_trackingFragment)
        }

        return binding.root
    }
}