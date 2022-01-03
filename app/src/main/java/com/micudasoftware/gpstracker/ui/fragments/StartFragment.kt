package com.micudasoftware.gpstracker.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {

    private val viewModel: MainViewModel by viewModels()

}