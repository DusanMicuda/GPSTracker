package com.micudasoftware.gpstracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.databinding.FragmentStartBinding
import com.micudasoftware.gpstracker.other.Constants.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION
import com.micudasoftware.gpstracker.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.ui.viewmodels.StartViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start), EasyPermissions.PermissionCallbacks {

    private val viewModel: StartViewModel by viewModels()
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(layoutInflater, container, false)

        binding.rvTracks.apply {
            adapter = viewModel.trackAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fab.setOnClickListener {
            if (Utils.hasLocationPermissions(requireContext()))
                findNavController().navigate(R.id.action_startFragment_to_trackingFragment)
            else {
                requestPermissions()
                requestBackgroundPermission()
            }
        }

        return binding.root
    }

    private fun requestPermissions() {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun requestBackgroundPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !EasyPermissions.hasPermissions(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
                    EasyPermissions.hasPermissions(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions at all the time to use this app on background.",
                    REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION)
            requestBackgroundPermission()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show()
        else
            requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}