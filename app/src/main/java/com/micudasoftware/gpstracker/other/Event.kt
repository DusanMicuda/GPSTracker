package com.micudasoftware.gpstracker.other

sealed class Event {
    data class Navigate(val route: Int) : Event()
    data class ShowToast(val message: String) : Event()
    data class SendCommandToService(val command: String) : Event()
}
