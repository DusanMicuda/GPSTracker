package com.micudasoftware.gpstracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.databinding.FragmentTrackingBinding
import com.micudasoftware.gpstracker.other.Event
import com.micudasoftware.gpstracker.services.TrackingService
import com.micudasoftware.gpstracker.ui.viewmodels.TrackingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: TrackingViewModel by viewModels()
    private lateinit var binding: FragmentTrackingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackingBinding.inflate(layoutInflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            viewModel.map = it
            viewModel.addAllPolylines()
        }

        binding.btnToggleTrack.setOnClickListener {
            viewModel.setMapViewSize(binding.mapView.width, binding.mapView.height)
            viewModel.toggleTrack()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.btnText.collect {
                binding.btnToggleTrack.text = it
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect { event ->
                when (event) {
                    is Event.Navigate -> {
                        findNavController().navigate(event.route)
                    }
                    is Event.SendCommandToService -> {
                        Intent(requireContext(), TrackingService::class.java).also {
                            it.action = event.command
                            requireContext().startService(it)
                        }
                    }
                    is Event.ShowToast -> {
                        Toast.makeText(requireActivity(), event.message, LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}