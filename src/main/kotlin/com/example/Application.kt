package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.google.gson.Gson

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {

        val server = DrawingServer()
        configureSessions()
        configureRouting()
        configureSerialization()
        configureSockets()
        configureMonitoring()
    }.start(wait = true)
}
