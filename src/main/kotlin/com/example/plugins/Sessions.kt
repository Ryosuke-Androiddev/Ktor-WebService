package com.example.plugins

import com.example.session.DrawingSession
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*

fun Application.configureSessions() {

    install(Sessions){
        cookie<DrawingSession>("SESSION")
    }
    intercept(ApplicationCallPipeline.Features){
        if (call.sessions.get<DrawingSession>() == null){
            val clientId = call.parameters["client_id"] ?: ""
            call.sessions.set(DrawingSession(clientId, generateNonce()))
        }
    }

}