package com.micudasoftware.gpstracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    val runsSortedByDate = mainRepository.getAllTracksSortedByDate()

    fun insertTrack(track: Track) = viewModelScope.launch {
        mainRepository.insertTrack(track)
    }
}