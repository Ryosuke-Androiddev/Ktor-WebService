package com.example.plugins

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