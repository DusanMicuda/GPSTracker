package com.micudasoftware.gpstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.micudasoftware.gpstracker.repositories.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPermissionsApi
@HiltViewModel
class StartViewModel @Inject constructor(
    mainRepository: MainRepositoryImpl
) : ViewModel() {

    var runsSortedByDate = mainRepository.getAllTracksSortedByDate()
        private set
}