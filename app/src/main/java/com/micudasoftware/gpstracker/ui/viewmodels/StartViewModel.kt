package com.micudasoftware.gpstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micudasoftware.gpstracker.repositories.MainRepositoryImpl
import com.micudasoftware.gpstracker.ui.adapters.TrackAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    mainRepository: MainRepositoryImpl
) : ViewModel() {

    private val runsSortedByDate = mainRepository.getAllTracksSortedByDate()
    val trackAdapter = TrackAdapter()

    init {
        viewModelScope.launch {
            runsSortedByDate.collect {
                trackAdapter.submitList(it)
            }
        }
    }
}