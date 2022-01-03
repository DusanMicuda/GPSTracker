package com.micudasoftware.gpstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.micudasoftware.gpstracker.repositories.MainRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

}