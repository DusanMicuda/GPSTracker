package com.micudasoftware.gpstracker.other

import com.micudasoftware.gpstracker.ui.screens.destinations.DirectionDestination

sealed class Event {
    data class Navigate(val route: DirectionDestination) : Event()
    data class ShowToast(val message: String) : Event()
    data class SendCommandToService(val command: String) : Event()
}
