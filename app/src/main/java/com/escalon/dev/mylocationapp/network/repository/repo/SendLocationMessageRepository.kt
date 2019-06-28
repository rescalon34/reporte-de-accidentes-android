package com.escalon.dev.mylocationapp.network.repository.repo

import com.escalon.dev.mylocationapp.network.LocationApi
import com.escalon.dev.mylocationapp.network.model.SendLocationMessage

class SendLocationMessageRepository(private val apiService: LocationApi) {

    /**
     * send location message
     */
    fun sendLocationMessage(sendLocationMessage: SendLocationMessage) = apiService.sendLocationMessage(
        sendLocationMessage.location, sendLocationMessage.message
    )
}