package com.micudasoftware.gpstracker.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()


}