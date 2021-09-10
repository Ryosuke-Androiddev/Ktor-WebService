package com.example.plugins

import com.example.DrawingServer
import com.example.Utility.Constants.MAX_ROOM_SIZE
import com.example.data.Room
import com.example.data.models.BasicApiResponse
import com.example.data.models.CreateRoomRequest
import createRoomRoute
import getRoomsRoute
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import joinRoomRoute

fun Application.configureRouting() {
    install(Routing){
        createRoomRoute()
        getRoomsRoute()
        joinRoomRoute()
        gameWebSocketRoute()
    }
}